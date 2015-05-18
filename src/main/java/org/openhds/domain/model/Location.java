
package org.openhds.domain.model;

import org.openhds.Description;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;


/**
 * @author Dave Roberge
 */
@Description(description = "A distinct Location in the study area, at a particular LocationHierarchy.")
@Entity
@Table(name = "location")
public class Location extends AuditableCollectedEntity implements Serializable {

    public final static long serialVersionUID = 169551578162260199L;

    @NotNull
    @Size(min = 1)
    @Description(description = "External Id of the location. This id is used internally.")
    private String extId;

    @NotNull
    @Size(min = 1)
    @Description(description = "Name of the location.")
    private String name;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private LocationHierarchy locationHierarchy = new LocationHierarchy();

    @Description(description = "The type of Location.")
    private String type;

    @Description(description = "The longitude for the Location")
    private String longitude;

    @Description(description = "The latitude for the Location")
    private String latitude;

    @Description(description = "How accurate are the longitude/latitude readings for the Location")
    private String accuracy;

    @Description(description = "The altitude for the Location")
    private String altitude;

    //@OneToMany(targetEntity = org.openhds.domain.model.Residency.class)
    //@JoinColumn(name = "location_uuid")
    //private List<Residency> residencies = new ArrayList<Residency>();

    // Extensions for bioko island project
    @Description(description = "The number of this building within a sector")
    private String buildingNumber;

    @Description(description = "The floor number within the building this location is associated with")
    private String floorNumber;

    @Description(description = "The name of the Region that contains this location")
    private String regionName;

    @Description(description = "The name of the Province that contains this location")
    private String provinceName;

    @Description(description = "The name of the Sub District that contains this location")
    private String subDistrictName;

    @Description(description = "The name of the District that contains this location")
    private String districtName;

    @Description(description = "The name of the map sector that contains this location")
    private String sectorName;

    @Description(description = "The name of the locality (aka AREA) that contains this location")
    private String localityName;

    @Description(description = "The name of the community that contains this location")
    private String communityName;

    @Description(description = "Formatted code for the community that contains this location")
    private String communityCode;

    @Description(description = "The name of the map area - disregarding Locality - that contains this location")
    private String mapAreaName;

    @Description(description = "A description of the observable features of a location")
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String id) {
        extId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocationHierarchy getLocationHierarchy() {
        return locationHierarchy;
    }

    public void setLocationHierarchy(LocationHierarchy hierarchy) {
        locationHierarchy = hierarchy;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longi) {
        longitude = longi;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String lat) {
        latitude = lat;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String acc) {
        accuracy = acc;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String alt) {
        altitude = alt;
    }

    //public List<Residency> getResidencies() {
    //    return residencies;
    //}

    //public void setResidencies(List<Residency> list) {
    //    residencies = list;
    //}

    public String getSectorName() {
        return sectorName;
    }

    public void setSectorName(String sectorName) {
        this.sectorName = sectorName;
    }

    public String getLocalityName() {
        return localityName;
    }

    public void setLocalityName(String localityName) {
        this.localityName = localityName;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getCommunityCode() {
        return communityCode;
    }

    public void setCommunityCode(String communityCode) {
        this.communityCode = communityCode;
    }

    public String getMapAreaName() {
        return mapAreaName;
    }

    public void setMapAreaName(String mapAreaName) {
        this.mapAreaName = mapAreaName;
    }

    public String getBuildingNumber() {
        return buildingNumber;
    }

    public void setBuildingNumber(String buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    public String getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(String floorNumber) {
        this.floorNumber = floorNumber;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getSubDistrictName() {
        return subDistrictName;
    }

    public void setSubDistrictName(String subDistrictName) {
        this.subDistrictName = subDistrictName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public static Location makeStub(String uuid, String extId) {

        Location stub = new Location();
        stub.setUuid(uuid);
        stub.setExtId(extId);
        return stub;

    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Location)) {
            return false;
        }

        final String otherUuid = ((Location) other).getUuid();
        return null != uuid && null != otherUuid && uuid.equals(otherUuid);
    }
}
