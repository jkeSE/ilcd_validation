package com.okworx.ilcd.validation.util;

import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

import com.okworx.ilcd.validation.common.Constants;

/**
 * <p>ILCDNameSpaceContext class.</p>
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public class ILCDNameSpaceContext implements NamespaceContext {

	/** {@inheritDoc} */
	public String getNamespaceURI(String prefix) {

		String namespaceUri = null;

		if (prefix.equals("common"))
			namespaceUri = Constants.NS_COMMON;

		if (prefix.equals("p"))
			namespaceUri = Constants.NS_PROCESS;

		else if (prefix.equals("l"))
			namespaceUri = Constants.NS_LCIAMETHOD;

		else if (prefix.equals("f"))
			namespaceUri = Constants.NS_FLOW;

		else if (prefix.equals("fp"))
			namespaceUri = Constants.NS_FLOWPROPERTY;

		else if (prefix.equals("u"))
			namespaceUri = Constants.NS_UNITGROUP;

		else if (prefix.equals("s"))
			namespaceUri = Constants.NS_SOURCE;

		else if (prefix.equals("c"))
			namespaceUri = Constants.NS_CONTACT;

		else if (prefix.equals("lcm"))
			namespaceUri = Constants.NS_LCMODEL;

		else if (prefix.equals("xml"))
			namespaceUri = "http://www.w3.org/XML/1998/namespace";

		return namespaceUri;
	}

	/** {@inheritDoc} */
	public String getPrefix(String namespaceUri) {
		String prefix = null;

		if (namespaceUri.equals(Constants.NS_COMMON))
			prefix = "common";

		else if (namespaceUri.equals(Constants.NS_PROCESS))
			prefix = "p";

		else if (namespaceUri.equals(Constants.NS_LCIAMETHOD))
			prefix = "l";

		else if (namespaceUri.equals(Constants.NS_FLOW))
			prefix = "f";

		else if (namespaceUri.equals(Constants.NS_FLOWPROPERTY))
			prefix = "fp";

		else if (namespaceUri.equals(Constants.NS_UNITGROUP))
			prefix = "u";

		else if (namespaceUri.equals(Constants.NS_SOURCE))
			prefix = "s";

		else if (namespaceUri.equals(Constants.NS_CONTACT))
			prefix = "c";

		else if (namespaceUri.equals(Constants.NS_LCMODEL))
			prefix = "lcm";

		else if (namespaceUri.equals("http://www.w3.org/XML/1998/namespace"));
			prefix = "xml";

		return prefix;
	}

	/** {@inheritDoc} */
	public Iterator<String> getPrefixes(String arg0) {
		return null;
	}

}
