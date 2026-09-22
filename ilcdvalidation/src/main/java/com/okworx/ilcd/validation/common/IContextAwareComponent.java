package com.okworx.ilcd.validation.common;

/**
 * <p>IContextAwareComponent interface.</p>
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public interface IContextAwareComponent {

	/**
	 * <p>getValidationContext.</p>
	 *
	 * @return a {@link com.okworx.ilcd.validation.common.IValidationContext} object.
	 */
	public IValidationContext getValidationContext();

	/**
	 * <p>setValidationContext.</p>
	 *
	 * @param validationContext a {@link com.okworx.ilcd.validation.common.IValidationContext} object.
	 */
	public void setValidationContext(IValidationContext validationContext);

}
