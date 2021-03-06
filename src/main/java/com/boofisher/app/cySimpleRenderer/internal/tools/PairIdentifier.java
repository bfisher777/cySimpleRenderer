package com.boofisher.app.cySimpleRenderer.internal.tools;

import com.boofisher.app.cySimpleRenderer.internal.tools.PairIdentifier;
import org.cytoscape.model.CyIdentifiable;

/**
 * Object that uniquely identifies a pair of CyIdentifiables
 * 
 * @author mkucera
 *
 */
public class PairIdentifier {
	private long suid1;
	private long suid2;
	
	PairIdentifier(CyIdentifiable id1, CyIdentifiable id2) {
		this(id1.getSUID(), id2.getSUID());
	}
	
	PairIdentifier(long id1, long id2) {
		if(id1 >= id2) {
			suid1 = id1;
			suid2 = id2;
		} else {
			suid1 = id2;
			suid2 = id1;
		}
	}

	// Generated by Eclipse
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (suid1 ^ (suid1 >>> 32));
		result = prime * result + (int) (suid2 ^ (suid2 >>> 32));
		return result;
	}

	// Generated by Eclipse
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PairIdentifier other = (PairIdentifier) obj;
		if (suid1 != other.suid1)
			return false;
		if (suid2 != other.suid2)
			return false;
		return true;
	}
}
