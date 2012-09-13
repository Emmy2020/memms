/** 
 * Copyright (c) 2012, Clinton Health Access Initiative.
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
package org.chai.memms.maintenance

import org.chai.memms.equipment.Equipment;

/**
 * @author Jean Kahigiso M.
 *
 */
class WorkOrder {
	
    enum Criticality{
		NORAMAL("normal"),
		LOW("low"),
		HIGH("high")
		String messageCode = "work.order.criticality"
		String name
		Criticality(String name){this.name=name}
		String getKey(){ return name() }
	}
	
	enum OrderStatus{
		OPEN("open"),
		CLOSEDFIXED("closedfixed"),
		CLOSEDFORDISPOSAL("closedfordisposal")
		String messageCode = "order.status"
		String name
		OrderStatus(String name){ this.name=name }
		String getKey() { return name() }
	}
	
	String currency
	String descriptions
	Integer estimatedCost
	Date openOn
	Date closedOn
	Boolean requestAssistance
	
	Criticality criticality
	OrderStatus status
	 
	static belongsTo = [equipment: Equipment]
	static hasMany = [comments: Comment, performedActions: MaintenanceProcess, materialsUsed: MaintenanceProcess]
	
	static constraints = {
		requestAssistance nullable: true
		descriptions nullable: false, blank: false
		openOn nullable: false, validation:{it <= new Date()}
		closedOn nullable: true, validation:{ val, obj ->
			if(val!=null)
				return ((val <= new Date()) && (val.after(obj.openOn) || (val.compareTo(obj.openOn)==0)))
			else return true
		}
		criticality nullable: false, blank: false, inList: [Criticality.LOW,Criticality.HIGH,Criticality.NORAMAL]
		status nullable: false, blank: false, inList:[OrderStatus.OPEN,OrderStatus.CLOSEDFIXED,OrderStatus.CLOSEDFORDISPOSAL]
		currency  nullable: true, blank: true, inList: ["RWF","USD","EUR"], validator:{ val, obj ->
			if(val == null && obj.donation == null) return false
		}
	}
	
	static mapping = {
		table "memms_work_order"
		version false
	}
	

	@Override
	public String toString() {
		return "WorkOrder [id=" + id + "]";
	}	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this.is(obj))
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WorkOrder other = (WorkOrder) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}












