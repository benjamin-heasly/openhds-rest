package org.openhds.service.impl;

import org.junit.Test;
import org.openhds.domain.model.ProjectCode;
import org.openhds.service.UuidServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.test.context.support.WithUserDetails;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

/**
 * Created by bsh on 7/13/15.
 */
public class ProjectCodeServiceTest extends UuidServiceTest<ProjectCode, ProjectCodeService> {

    @Autowired
    @Override
    protected void initialize(ProjectCodeService service) {
        this.service = service;
    }

    @Override
    protected ProjectCode makeInvalidEntity() {
        return new ProjectCode();
    }

    @Test(expected = DataIntegrityViolationException.class)
    @WithUserDetails
    public void noDuplicateNames() throws Exception {
        ProjectCode codeA = service.createCode("a", "value-a", "group-1");
        ProjectCode codeB = service.createCode("a", "value-b", "group-2");
    }

    @Test
    @WithUserDetails
    public void getByGroup() throws Exception {
        ProjectCode codeA = service.createCode("a", "value-a", "group-1");
        ProjectCode codeB = service.createCode("b", "value-b", "group-2");
        ProjectCode codeC = service.createCode("c", "value-c", "group-1");

        List<ProjectCode> group1 = service.findByCodeGroup("group-1");
        assertEquals(2, group1.size());
        assertTrue(group1.contains(codeA));
        assertTrue(group1.contains(codeC));

        List<ProjectCode> group2 = service.findByCodeGroup("group-2");
        assertEquals(1, group2.size());
        assertTrue(group2.contains(codeB));

        List<ProjectCode> notAGroup = service.findByCodeGroup("not-a-group");
        assertEquals(0, notAGroup.size());
    }

    @Test
    @WithUserDetails
    public void valueLookup() throws Exception {
        ProjectCode codeA = service.createCode("a", "value-a", "group-1");
        ProjectCode codeB = service.createCode("b", "value-b", "group-2");
        assertEquals("value-a", service.getValueForCodeName("a"));
        assertEquals("value-b", service.getValueForCodeName("b"));
    }

    @Test(expected = NoSuchElementException.class)
    @WithUserDetails
    public void missingValueLookup() throws Exception {
        ProjectCode codeA = service.createCode("a", "value-a", "group-1");
        ProjectCode codeB = service.createCode("b", "value-b", "group-2");
        assertEquals("value-c", service.getValueForCodeName("c"));
    }

    @Test
    @WithUserDetails
    public void groupMembership() throws Exception {
        ProjectCode codeA = service.createCode("a", "value-a", "group-1");
        ProjectCode codeB = service.createCode("b", "value-b", "group-2");
        ProjectCode codeC = service.createCode("c", "value-c", "group-1");

        assertTrue(service.isValueInCodeGroup("value-a", "group-1"));
        assertTrue(service.isValueInCodeGroup("value-c", "group-1"));
        assertTrue(service.isValueInCodeGroup("value-b", "group-2"));

        assertFalse(service.isValueInCodeGroup("value-a", "group-2"));
        assertFalse(service.isValueInCodeGroup("value-c", "group-2"));
        assertFalse(service.isValueInCodeGroup("value-b", "group-1"));
        assertFalse(service.isValueInCodeGroup("not-a-value", "group-1"));
        assertFalse(service.isValueInCodeGroup("not-a-value", "group-2"));

        assertFalse(service.isValueInCodeGroup("value-a", "not-a-group"));
        assertFalse(service.isValueInCodeGroup("not-a-value", "not-a-group"));
    }
}