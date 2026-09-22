package com.okworx.ilcd.validation.test.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.okworx.ilcd.validation.events.EventsList;
import com.okworx.ilcd.validation.events.Severity;
import com.okworx.ilcd.validation.events.ValidationEvent;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EventsListTest {

	public static final String ASPECT = "TEST";

	@Test
	public void testHasErrors() {
		EventsList el = new EventsList(ASPECT);
		addErrors(el);
		addWarnings(el);
		addSuccesses(el);
		assertTrue(el.hasErrors());
	}

	@Test
	public void testHasWarnings() {
		EventsList el = new EventsList(ASPECT);
		addErrors(el);
		addWarnings(el);
		addSuccesses(el);
		assertTrue(el.hasWarnings());
	}

	@Test
	public void testIsPositive() {
		EventsList el = new EventsList(ASPECT);
		addErrors(el);
		addWarnings(el);
		assertFalse(el.isPositive());

		EventsList el1 = new EventsList(ASPECT);
		addSuccesses(el1);
		assertTrue(el1.isPositive());
	}
	
	@Test 
	public void testCountErrors() {
		EventsList el = new EventsList(ASPECT);
		addErrors(el);
		addWarnings(el);
		assertEquals(3, el.getErrorCount().intValue());
		assertEquals(4, el.getWarningCount().intValue());
	}

	private void addErrors(EventsList el) {
		el.add(new ValidationEvent(ASPECT, Severity.ERROR, null, ""));
		el.add(new ValidationEvent(ASPECT, Severity.ERROR, null, ""));
		el.add(new ValidationEvent(ASPECT, Severity.ERROR, null, ""));
	}

	private void addWarnings(EventsList el) {
		el.add(new ValidationEvent(ASPECT, Severity.WARNING, null, ""));
		el.add(new ValidationEvent(ASPECT, Severity.WARNING, null, ""));
		el.add(new ValidationEvent(ASPECT, Severity.WARNING, null, ""));
		el.add(new ValidationEvent(ASPECT, Severity.WARNING, null, ""));
	}

	private void addSuccesses(EventsList el) {
		el.add(new ValidationEvent(ASPECT, Severity.SUCCESS, null, ""));
		el.add(new ValidationEvent(ASPECT, Severity.SUCCESS, null, ""));
		el.add(new ValidationEvent(ASPECT, Severity.SUCCESS, null, ""));
		el.add(new ValidationEvent(ASPECT, Severity.SUCCESS, null, ""));
		el.add(new ValidationEvent(ASPECT, Severity.SUCCESS, null, ""));
	}
}
