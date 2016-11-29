package org.openhds;

import com.google.common.collect.Sets;
import org.junit.Test;
import org.openhds.domain.model.census.Location;
import org.openhds.domain.model.update.Visit;
import org.openhds.domain.util.DeleteQuery;

import java.time.ZonedDateTime;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class DeleteQueryTest {

    @Test
    public void oneEntityWithNoDependencies_getDependentEntities_returnsNoDependencies() {
        DeleteQuery underTest = new DeleteQuery();
        Location visitLocation = new Location();
        visitLocation.setUuid("foo");
        ZonedDateTime now = ZonedDateTime.now();

        String deletableVisitUuid = "some-uuid";
        Visit deleteableVisit = new Visit();
        deleteableVisit.setUuid(deletableVisitUuid);
        deleteableVisit.setLocation(visitLocation);
        deleteableVisit.setVisitDate(now);
        underTest.addEntity(deleteableVisit);


        Set<Visit> expected = Sets.newHashSet();
        assertEquals(expected, underTest.getDependentEntities(deletableVisitUuid));
    }

    @Test
    public void twoDependentEntities_getDependentEntities_returnsDependentEntity() {
        DeleteQuery underTest = new DeleteQuery();

        String deleteableVisitUuid = "deletable-uuid";
        String dependentVisitUuid = "dependent-uuid";

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime then = now.minusDays(1);

        Location visitLocation = new Location();
        visitLocation.setUuid("foo");

        Visit deletableVisit = new Visit();
        Visit dependentVisit = new Visit();

        deletableVisit.setUuid(deleteableVisitUuid);
        deletableVisit.setVisitDate(now);
        deletableVisit.setLocation(visitLocation);
        dependentVisit.setUuid(dependentVisitUuid);
        dependentVisit.setVisitDate(then);
        dependentVisit.setLocation(visitLocation);

        underTest.addEntity(deletableVisit);
        underTest.addEntity(dependentVisit);

        Set<Visit> expected = Sets.newHashSet(deletableVisit);

        assertEquals(expected, underTest.getDependentEntities(dependentVisitUuid));
    }

    //addVisit throws exception if visitUuid is not set

}
