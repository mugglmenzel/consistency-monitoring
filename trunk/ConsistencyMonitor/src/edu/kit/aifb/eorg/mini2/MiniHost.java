/**
 * 
 */
package edu.kit.aifb.eorg.mini2;

import java.io.Serializable;

/**
 * @author David Bermbach
 * 
 *         created on: 30.04.2012
 */
public class MiniHost implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String host;
	public int port;

	public MiniHost(String host, int prot) {
		this.host = host;
		this.port = port;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object arg) {
		if (!(arg instanceof MiniHost))
			return false;
		MiniHost arg2 = (MiniHost) arg;
		return this.host.equals(arg2.host) && this.port == arg2.port;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return host.hashCode() + port;
	}

}
