
package org.openhds.domain.model.census;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.openhds.domain.contract.AuditableExtIdEntity;
import org.openhds.domain.util.Description;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


/**
 * @author Dave Roberge
 */
@Description(description = "A distinct Location in the study area, at a particular LocationHierarchy.")
@Entity
@Table(name = "location", indexes={
        @Index(columnList = "lastModifiedDate"),
        @Index(columnList = "extId")})
public class Location extends AuditableExtIdEntity implements Serializable {

    public final static long serialVersionUID = 169551578162260199L;

    @NotNull(message = "Location cannot have a null name.")
    @Size(min = 1)
    @Description(description = "Name of the location.")
    private String name;

    @NotNull(message = "Location cannot have a null type.")
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

    @NotNull(message = "Location cannot have a null parent.")
    @ManyToOne
    private LocationHierarchy locationHierarchy;

    @JsonIgnore
    @Description(description = "The set of all residencies where this Individual lives or lived.")
    @OneToMany(mappedBy = "location")
    private Set<Residency> residencies = new HashSet<>();

    //TODO: Do we want to keep these? trim them down?
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public LocationHierarchy getLocationHierarchy() {
        return locationHierarchy;
    }

    public void setLocationHierarchy(LocationHierarchy locationHierarchy) {
        this.locationHierarchy = locationHierarchy;
    }

    public Set<Residency> getResidencies() {
        return residencies;
    }

    public void setResidencies(Set<Residency> residencies) {
        this.residencies = residencies;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Location{" +
                "name='" + name + '\'' +
                ", locationHierarchy=" + locationHierarchy +
                ", type='" + type + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", accuracy='" + accuracy + '\'' +
                ", altitude='" + altitude + '\'' +
                ", buildingNumber='" + buildingNumber + '\'' +
                ", floorNumber='" + floorNumber + '\'' +
                ", regionName='" + regionName + '\'' +
                ", provinceName='" + provinceName + '\'' +
                ", subDistrictName='" + subDistrictName + '\'' +
                ", districtName='" + districtName + '\'' +
                ", sectorName='" + sectorName + '\'' +
                ", localityName='" + localityName + '\'' +
                ", communityName='" + communityName + '\'' +
                ", communityCode='" + communityCode + '\'' +
                ", mapAreaName='" + mapAreaName + '\'' +
                ", description='" + description + '\'' +
                "} " + super.toString();
    }
}
