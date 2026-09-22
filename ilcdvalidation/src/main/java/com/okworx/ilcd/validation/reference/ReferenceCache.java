package com.okworx.ilcd.validation.reference;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;

import org.apache.logging.log4j.Logger;

import com.okworx.ilcd.validation.common.DefaultValidationContext;
import com.okworx.ilcd.validation.common.IContextAwareComponent;
import com.okworx.ilcd.validation.common.IValidationContext;

/**
 * <p>ReferenceCache class.</p>
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public class ReferenceCache implements IContextAwareComponent {

	protected final Logger log = org.apache.logging.log4j.LogManager.getLogger(this
			.getClass());

	private HashMap<String, IDatasetReference> references = new HashMap<String, IDatasetReference>();

	private IValidationContext validationContext = new DefaultValidationContext();
			
	/**
	 * <p>getLinks.</p>
	 *
	 * @return a {@link java.util.HashMap} object.
	 */
	public HashMap<String, IDatasetReference> getLinks() {
		return references;
	}
	
	/**
	 * <p>put.</p>
	 *
	 * @param objects a {@link java.util.HashMap} object.
	 */
	public void put(HashMap<String, IDatasetReference> objects) {
		this.references.putAll( objects );
	}

	/**
	 * <p>initializeFromDir.</p>
	 *
	 * @param directory a {@link java.io.File} object.
	 */
	public void initializeFromDir(File directory) {

		ReferenceBuilder builder = new ReferenceBuilder();
		
		builder.setValidationContext(validationContext);

		builder.build(directory);

		this.references = builder.getReferences();

	}

	/**
	 * <p>initializeFromFiles.</p>
	 *
	 * @param files a {@link java.util.Collection} object.
	 */
	public void initializeFromFiles(Collection<File> files) {
	}

	/**
	 * <p>initializeFromURIs.</p>
	 *
	 * @param files a {@link java.util.Collection} object.
	 */
	public void initializeFromURIs(Collection<URI> files) {
	}

	/**
	 * <p>initializeFromArchive.</p>
	 *
	 * @param archive a {@link java.io.File} object.
	 */
	public void initializeFromArchive(File archive) {
		initializeFromDir(archive);
	}

	/**
	 * <p>contains.</p>
	 *
	 * @param reference a {@link com.okworx.ilcd.validation.reference.IDatasetReference} object.
	 * @return a boolean.
	 */
	public boolean contains(IDatasetReference reference) {
		return references.containsKey(reference.getUuid());
	}

	/**
	 * <p>contains.</p>
	 *
	 * @param uuid a {@link java.lang.String} object.
	 * @return a boolean.
	 */
	public boolean contains(String uuid) {
		return references.containsKey(uuid);
	}

	/**
	 * <p>Getter for the field <code>validationContext</code>.</p>
	 *
	 * @return a {@link com.okworx.ilcd.validation.common.IValidationContext} object.
	 */
	public IValidationContext getValidationContext() {
		return validationContext;
	}

	/** {@inheritDoc} */
	public void setValidationContext(IValidationContext validationContext) {
		this.validationContext = validationContext;
	}
}
