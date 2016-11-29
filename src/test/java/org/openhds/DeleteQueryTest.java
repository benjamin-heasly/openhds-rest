package org.openhds;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.openhds.domain.model.census.Location;
import org.openhds.domain.model.update.Visit;
import org.openhds.domain.util.DeleteQuery;

import java.time.ZonedDateTime;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class DeleteQueryTest {

    private DeleteQuery underTest;
    private Location visitLocation;
    private String deleteableVisitUuid = "deletable-uuid";
    private String dependentVisitUuid = "dependent-uuid";
    private Visit deletableVisit = new Visit();
    private Visit dependentVisit = new Visit();

    @Before
    public void setUp() {
        underTest = new DeleteQuery();
        visitLocation = new Location();
        visitLocation.setUuid("foo");
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime then = now.minusDays(1);
        deletableVisit.setUuid(deleteableVisitUuid);
        deletableVisit.setVisitDate(now);
        deletableVisit.setLocation(visitLocation);
        dependentVisit.setUuid(dependentVisitUuid);
        dependentVisit.setVisitDate(then);
        dependentVisit.setLocation(visitLocation);
    }

    @Test
    public void oneEntityWithNoDependencies_getDependentEntities_returnsNoDependencies() {
        underTest.addEntity(deletableVisit);

        Set<Visit> expected = Sets.newHashSet();

        assertEquals(expected, underTest.getDependentEntities(deleteableVisitUuid));
    }

    @Test
    public void twoDependentEntities_getDependentEntities_returnsDependentEntity() {
        underTest.addEntity(deletableVisit);
        underTest.addEntity(dependentVisit);

        Set<Visit> expected = Sets.newHashSet(deletableVisit);

        assertEquals(expected, underTest.getDependentEntities(dependentVisitUuid));
    }

    @Test(expected=IllegalArgumentException.class)
    public void addVisitThrowsExceptionIfVisitUuidIsNotSet() {
        DeleteQuery underTest = new DeleteQuery();

        Visit someVisit = new Visit();

        underTest.addEntity(someVisit);
    }

}
