package org.chai.memms

/**
* Copyright (c) 2011, Clinton Health Access Initiative.
*
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*     * Redistributions of source code must retain the above copyright
*       notice, this list of conditions and the following disclaimer.
*     * Redistributions in binary form must reproduce the above copyright
*       notice, this list of conditions and the following disclaimer in the
*       documentation and/or other materials provided with the distribution.
*     * Neither the name of the <organization> nor the
*       names of its contributors may be used to endorse or promote products
*       derived from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
* ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
* DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
import grails.plugin.spock.IntegrationSpec;
import java.util.Date
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.util.WebUtils;
import javax.servlet.ServletRequest;
import org.chai.memms.security.User;
import org.chai.memms.security.UserType;

import org.apache.commons.logging.Log;
import org.chai.memms.location.DataLocation;
import org.chai.memms.location.DataLocationType;
import org.chai.memms.location.Location;
import org.chai.memms.location.LocationLevel;

import org.codehaus.groovy.grails.commons.ConfigurationHolder as CONF

abstract class IntegrationTests extends IntegrationSpec {
	
	def refreshValueService
	def springcacheService
	def sessionFactory
	
	static final String CODE (def number) { return "CODE"+number }
	static final String HEALTH_CENTER_GROUP = "Health Center"
	static final String DISTRICT_HOSPITAL_GROUP = "District Hospital"
	
	static final String NATIONAL = "National"
	static final String PROVINCE = "Province"
	static final String DISTRICT = "District"
	static final String SECTOR = "Sector"
	
	static final String RWANDA = "Rwanda"
	static final String KIGALI_CITY = "Kigali City"
	static final String NORTH = "North"
	static final String BURERA = "Burera"
	static final String BUTARO = "Butaro DH"
	static final String KIVUYE = "Kivuye HC"
	
	
	def setup() {
		// using cache.use_second_level_cache = false in test mode doesn't work so
		// we flush the cache after each test
		//springcacheService.flushAll()
	}
	
	static def setupLocationTree() {
		// for the test environment, the location level is set to 4
		// so we create a tree accordingly
		
		def hc = newDataLocationType(['en':HEALTH_CENTER_GROUP], HEALTH_CENTER_GROUP);
		def dh = newDataLocationType(['en':DISTRICT_HOSPITAL_GROUP], DISTRICT_HOSPITAL_GROUP);
		
		def country = newLocationLevel(['en':NATIONAL], NATIONAL)
		def province = newLocationLevel(['en':PROVINCE], PROVINCE)
		def district = newLocationLevel(['en':DISTRICT], DISTRICT)
		def sector = newLocationLevel(['en':SECTOR], SECTOR)
			
		def rwanda = newLocation(['en':RWANDA], RWANDA,null,country)
		def north = newLocation(['en':NORTH], NORTH, rwanda, province)
		def burera = newLocation(['en':BURERA], BURERA, north, district)

		
		newDataLocation(['en':BUTARO], BUTARO, burera, dh)
		newDataLocation(['en':KIVUYE], KIVUYE, burera, hc)
	}
	
	static def newLocation(def names, def code, def parent, def level) {
		def location = new Location(code: code, parent: parent, level: level)
		setLocaleValueInMap(location,names,"Names")
		location.save(failOnError: true)
		level.addToLocations(location)
		level.save(failOnError: true)
		if (parent != null) {
			parent.addToChildren(location)
			parent.save(failOnError: true)
		}
		return location
	}
	
	
	static def newDataLocation(def names, def code, def location, def type) {
		def dataLocation = new DataLocation(code: code, location: location, type: type)
		setLocaleValueInMap(dataLocation,names,"Names")
		dataLocation.save(failOnError: true)
		if (location != null) {
			location.addToDataLocations(dataLocation)
			location.save(failOnError: true)
		}
		if (type != null) {
			type.addToDataLocations(dataLocation)
			type.save(failOnError: true)
	   }
		return dataLocation
	}
	
	static def newDataLocationType(def names, def code) {
		def dataLocationType = new DataLocationType(code: code)
		setLocaleValueInMap(dataLocationType,names,"Names")
		return dataLocationType.save(failOnError: true)
	}
		
	static def newLocationLevel(def names, def code) {
		def locationLevel = new LocationLevel(code: code)
		setLocaleValueInMap(locationLevel,names,"Names")
		return locationLevel.save(failOnError: true)
	}
	
	
	static def getLocationLevels(def levels) {
		def result = []
		for (def level : levels) {
			result.add LocationLevel.findByCode(level)
		}
		return result;
	}
	
	static def getCalculationLocation(def code) {
		def location = Location.findByCode(code)
		if (location == null) location = DataLocation.findByCode(code)
		return location
	}
	
	static def getLocations(def codes) {
		def result = []
		for (String code : codes) {
			result.add(Location.findByCode(code))
		}
		return result
	}
	
	static def getDataLocations(def codes) {
		def result = []
		for (String code : codes) {
			result.add(DataLocation.findByCode(code))
		}
		return result
	}
	static def getDataLocationTypes(def codes){
		def result=[]
		for(String code: codes)
			result.add(DataLocationType.findByCode(code));
		return result;
	}
	
	static def newUser(def username, def uuid) {
		return new User(userType: UserType.OTHER, code: username, username: username, permissionString: '', passwordHash:'', uuid: uuid, firstname: 'first', lastname: 'last', organisation: 'org', phoneNumber: '+250 11 111 11 11').save(failOnError: true)
	}
	
	static def newUser(def username, def active, def confirmed) {
		return new User(userType: UserType.OTHER, code: 'not_important', username: username, email: username,
			passwordHash: '', active: active, confirmed: confirmed, uuid: 'uuid', firstname: 'first', lastname: 'last',
			organisation: 'org', phoneNumber: '+250 11 111 11 11').save(failOnError: true)
	}
	
	static def newUser(def username, def passwordHash, def active, def confirmed) {
		return new User(userType: UserType.OTHER, code: 'not_important', username: username, email: username,
			passwordHash: passwordHash, active: active, confirmed: confirmed, uuid: 'uuid', firstname: 'first', lastname: 'last',
			organisation: 'org', phoneNumber: '+250 11 111 11 11').save(failOnError: true)
	}
	
	def setupSecurityManager(def user) {
		def subject = [getPrincipal: { user?.uuid }, isAuthenticated: { user==null?false:true }, login: { token -> null }] as Subject
		ThreadContext.put( ThreadContext.SECURITY_MANAGER_KEY, [ getSubject: { subject } ] as SecurityManager )
		SecurityUtils.metaClass.static.getSubject = { subject }
		WebUtils.metaClass.static.getSavedRequest = { ServletRequest request -> null }
	}
	
	
	
	/**
	 * fieldName has to start with capital letter as 
	 * it is used to create setter of the object field
	 * @param object
	 * @param map
	 * @param fieldName
	 * @return
	 */
	static def setLocaleValueInMap(def object, def map, def fieldName){
		def methodName = 'set'+fieldName
		CONF.config.i18nFields.locales.each{ loc ->
			if(map.get(loc) != null)
				object."$methodName"(map.get(loc),new Locale(loc))
			else
				object."$methodName"("",new Locale(loc))	
		}
	}
}
