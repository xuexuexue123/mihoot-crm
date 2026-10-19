package com.ruoyi.crm.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 越南工业园区 crm_industrial_park
 */
public class CrmIndustrialPark extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String id;

    private String nameCn;
    private String nameNative;
    private String region;
    private String province;
    private String address;
    private String coordinates;
    private Double lat;
    private Double lng;
    private String establishedDate;
    private String developer;
    private String website;

    private BigDecimal totalAreaHa;
    private BigDecimal developedAreaHa;
    private BigDecimal occupancyRate;
    private List<String> dominantIndustries = new ArrayList<>();
    private List<String> encouragedIndustries = new ArrayList<>();
    private String parkLevel;
    private List<String> anchorTenants = new ArrayList<>();

    private BigDecimal landRentUsdPerSqm;
    private Integer leaseTermYears;
    private Integer leaseExpiryYear;
    private String operationExpiryDate;
    private BigDecimal standardFactoryRentUsdPerSqmMonth;
    private BigDecimal managementFeeUsdPerSqmYear;
    private String electricityRateUsdPerKwh;
    private BigDecimal waterRateUsdPerCum;
    private BigDecimal blueCollarWageUsd;
    private BigDecimal whiteCollarWageUsd;

    private String citIncentive;
    private String importTariffIncentive;
    private Boolean isSez;
    private String specialIncentives;
    private String competitiveAdvantages;

    private BigDecimal distanceToSeaPortKm;
    private String seaPortName;
    private BigDecimal distanceToAirportKm;
    private String airportName;
    private BigDecimal distanceToChinaBorderKm;
    private String borderPortName;
    private String nearestHighwayName;
    private BigDecimal distanceToHighwayKm;
    private String nearestRailwayName;
    private BigDecimal distanceToRailwayKm;
    private String mainRoadSpec;
    private String internalRoadSpec;
    private String powerReliability;
    private String powerSupplyDetail;
    private Boolean hasNationalGrid;
    private Boolean hasStandbyPower;
    private String waterSupplySource;
    private String waterSupplyCapacity;
    private String sewageTreatmentCapacity;
    private Boolean hasEmployeeDormitory;
    private String surroundingAmenities;

    private String laborSupply;
    private String recruitmentSources;
    private Boolean hasVocationalSchool;

    private String masterPlanUrl;
    private String brochureUrl;
    private List<String> photos = new ArrayList<>();
    private String dataSource;

    /** YYYY-MM-DD */
    private String lastUpdated;
    private String verificationStatus;
    private String consultantContact;
    private String contactPageUrl;
    private String officialPhone;
    private String officialEmail;
    private String operatingHours;

    private String delFlag;

    /** 列表查询关键字（非表字段） */
    @JsonIgnore
    private String searchQuery;

    @JsonIgnore
    private String dominantIndustry;

    @JsonIgnore
    private BigDecimal maxLandRent;

    @JsonIgnore
    private BigDecimal minOccupancy;

    @JsonIgnore
    private BigDecimal maxDistancePort;

    @JsonIgnore
    private Boolean isSezOnly;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getNameCn()
    {
        return nameCn;
    }

    public void setNameCn(String nameCn)
    {
        this.nameCn = nameCn;
    }

    public String getNameNative()
    {
        return nameNative;
    }

    public void setNameNative(String nameNative)
    {
        this.nameNative = nameNative;
    }

    public String getRegion()
    {
        return region;
    }

    public void setRegion(String region)
    {
        this.region = region;
    }

    public String getProvince()
    {
        return province;
    }

    public void setProvince(String province)
    {
        this.province = province;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getCoordinates()
    {
        return coordinates;
    }

    public void setCoordinates(String coordinates)
    {
        this.coordinates = coordinates;
    }

    public Double getLat()
    {
        return lat;
    }

    public void setLat(Double lat)
    {
        this.lat = lat;
    }

    public Double getLng()
    {
        return lng;
    }

    public void setLng(Double lng)
    {
        this.lng = lng;
    }

    public String getEstablishedDate()
    {
        return establishedDate;
    }

    public void setEstablishedDate(String establishedDate)
    {
        this.establishedDate = establishedDate;
    }

    public String getDeveloper()
    {
        return developer;
    }

    public void setDeveloper(String developer)
    {
        this.developer = developer;
    }

    public String getWebsite()
    {
        return website;
    }

    public void setWebsite(String website)
    {
        this.website = website;
    }

    public BigDecimal getTotalAreaHa()
    {
        return totalAreaHa;
    }

    public void setTotalAreaHa(BigDecimal totalAreaHa)
    {
        this.totalAreaHa = totalAreaHa;
    }

    public BigDecimal getDevelopedAreaHa()
    {
        return developedAreaHa;
    }

    public void setDevelopedAreaHa(BigDecimal developedAreaHa)
    {
        this.developedAreaHa = developedAreaHa;
    }

    public BigDecimal getOccupancyRate()
    {
        return occupancyRate;
    }

    public void setOccupancyRate(BigDecimal occupancyRate)
    {
        this.occupancyRate = occupancyRate;
    }

    public List<String> getDominantIndustries()
    {
        return dominantIndustries;
    }

    public void setDominantIndustries(List<String> dominantIndustries)
    {
        this.dominantIndustries = dominantIndustries != null ? dominantIndustries : new ArrayList<>();
    }

    public List<String> getEncouragedIndustries()
    {
        return encouragedIndustries;
    }

    public void setEncouragedIndustries(List<String> encouragedIndustries)
    {
        this.encouragedIndustries = encouragedIndustries != null ? encouragedIndustries : new ArrayList<>();
    }

    public String getParkLevel()
    {
        return parkLevel;
    }

    public void setParkLevel(String parkLevel)
    {
        this.parkLevel = parkLevel;
    }

    public List<String> getAnchorTenants()
    {
        return anchorTenants;
    }

    public void setAnchorTenants(List<String> anchorTenants)
    {
        this.anchorTenants = anchorTenants != null ? anchorTenants : new ArrayList<>();
    }

    public BigDecimal getLandRentUsdPerSqm()
    {
        return landRentUsdPerSqm;
    }

    public void setLandRentUsdPerSqm(BigDecimal landRentUsdPerSqm)
    {
        this.landRentUsdPerSqm = landRentUsdPerSqm;
    }

    public Integer getLeaseTermYears()
    {
        return leaseTermYears;
    }

    public void setLeaseTermYears(Integer leaseTermYears)
    {
        this.leaseTermYears = leaseTermYears;
    }

    public Integer getLeaseExpiryYear()
    {
        return leaseExpiryYear;
    }

    public void setLeaseExpiryYear(Integer leaseExpiryYear)
    {
        this.leaseExpiryYear = leaseExpiryYear;
    }

    public String getOperationExpiryDate()
    {
        return operationExpiryDate;
    }

    public void setOperationExpiryDate(String operationExpiryDate)
    {
        this.operationExpiryDate = operationExpiryDate;
    }

    public BigDecimal getStandardFactoryRentUsdPerSqmMonth()
    {
        return standardFactoryRentUsdPerSqmMonth;
    }

    public void setStandardFactoryRentUsdPerSqmMonth(BigDecimal standardFactoryRentUsdPerSqmMonth)
    {
        this.standardFactoryRentUsdPerSqmMonth = standardFactoryRentUsdPerSqmMonth;
    }

    public BigDecimal getManagementFeeUsdPerSqmYear()
    {
        return managementFeeUsdPerSqmYear;
    }

    public void setManagementFeeUsdPerSqmYear(BigDecimal managementFeeUsdPerSqmYear)
    {
        this.managementFeeUsdPerSqmYear = managementFeeUsdPerSqmYear;
    }

    public String getElectricityRateUsdPerKwh()
    {
        return electricityRateUsdPerKwh;
    }

    public void setElectricityRateUsdPerKwh(String electricityRateUsdPerKwh)
    {
        this.electricityRateUsdPerKwh = electricityRateUsdPerKwh;
    }

    public BigDecimal getWaterRateUsdPerCum()
    {
        return waterRateUsdPerCum;
    }

    public void setWaterRateUsdPerCum(BigDecimal waterRateUsdPerCum)
    {
        this.waterRateUsdPerCum = waterRateUsdPerCum;
    }

    public BigDecimal getBlueCollarWageUsd()
    {
        return blueCollarWageUsd;
    }

    public void setBlueCollarWageUsd(BigDecimal blueCollarWageUsd)
    {
        this.blueCollarWageUsd = blueCollarWageUsd;
    }

    public BigDecimal getWhiteCollarWageUsd()
    {
        return whiteCollarWageUsd;
    }

    public void setWhiteCollarWageUsd(BigDecimal whiteCollarWageUsd)
    {
        this.whiteCollarWageUsd = whiteCollarWageUsd;
    }

    public String getCitIncentive()
    {
        return citIncentive;
    }

    public void setCitIncentive(String citIncentive)
    {
        this.citIncentive = citIncentive;
    }

    public String getImportTariffIncentive()
    {
        return importTariffIncentive;
    }

    public void setImportTariffIncentive(String importTariffIncentive)
    {
        this.importTariffIncentive = importTariffIncentive;
    }

    public Boolean getIsSez()
    {
        return isSez;
    }

    public void setIsSez(Boolean isSez)
    {
        this.isSez = isSez;
    }

    public String getSpecialIncentives()
    {
        return specialIncentives;
    }

    public void setSpecialIncentives(String specialIncentives)
    {
        this.specialIncentives = specialIncentives;
    }

    public String getCompetitiveAdvantages()
    {
        return competitiveAdvantages;
    }

    public void setCompetitiveAdvantages(String competitiveAdvantages)
    {
        this.competitiveAdvantages = competitiveAdvantages;
    }

    public BigDecimal getDistanceToSeaPortKm()
    {
        return distanceToSeaPortKm;
    }

    public void setDistanceToSeaPortKm(BigDecimal distanceToSeaPortKm)
    {
        this.distanceToSeaPortKm = distanceToSeaPortKm;
    }

    public String getSeaPortName()
    {
        return seaPortName;
    }

    public void setSeaPortName(String seaPortName)
    {
        this.seaPortName = seaPortName;
    }

    public BigDecimal getDistanceToAirportKm()
    {
        return distanceToAirportKm;
    }

    public void setDistanceToAirportKm(BigDecimal distanceToAirportKm)
    {
        this.distanceToAirportKm = distanceToAirportKm;
    }

    public String getAirportName()
    {
        return airportName;
    }

    public void setAirportName(String airportName)
    {
        this.airportName = airportName;
    }

    public BigDecimal getDistanceToChinaBorderKm()
    {
        return distanceToChinaBorderKm;
    }

    public void setDistanceToChinaBorderKm(BigDecimal distanceToChinaBorderKm)
    {
        this.distanceToChinaBorderKm = distanceToChinaBorderKm;
    }

    public String getBorderPortName()
    {
        return borderPortName;
    }

    public void setBorderPortName(String borderPortName)
    {
        this.borderPortName = borderPortName;
    }

    public String getNearestHighwayName()
    {
        return nearestHighwayName;
    }

    public void setNearestHighwayName(String nearestHighwayName)
    {
        this.nearestHighwayName = nearestHighwayName;
    }

    public BigDecimal getDistanceToHighwayKm()
    {
        return distanceToHighwayKm;
    }

    public void setDistanceToHighwayKm(BigDecimal distanceToHighwayKm)
    {
        this.distanceToHighwayKm = distanceToHighwayKm;
    }

    public String getNearestRailwayName()
    {
        return nearestRailwayName;
    }

    public void setNearestRailwayName(String nearestRailwayName)
    {
        this.nearestRailwayName = nearestRailwayName;
    }

    public BigDecimal getDistanceToRailwayKm()
    {
        return distanceToRailwayKm;
    }

    public void setDistanceToRailwayKm(BigDecimal distanceToRailwayKm)
    {
        this.distanceToRailwayKm = distanceToRailwayKm;
    }

    public String getMainRoadSpec()
    {
        return mainRoadSpec;
    }

    public void setMainRoadSpec(String mainRoadSpec)
    {
        this.mainRoadSpec = mainRoadSpec;
    }

    public String getInternalRoadSpec()
    {
        return internalRoadSpec;
    }

    public void setInternalRoadSpec(String internalRoadSpec)
    {
        this.internalRoadSpec = internalRoadSpec;
    }

    public String getPowerReliability()
    {
        return powerReliability;
    }

    public void setPowerReliability(String powerReliability)
    {
        this.powerReliability = powerReliability;
    }

    public String getPowerSupplyDetail()
    {
        return powerSupplyDetail;
    }

    public void setPowerSupplyDetail(String powerSupplyDetail)
    {
        this.powerSupplyDetail = powerSupplyDetail;
    }

    public Boolean getHasNationalGrid()
    {
        return hasNationalGrid;
    }

    public void setHasNationalGrid(Boolean hasNationalGrid)
    {
        this.hasNationalGrid = hasNationalGrid;
    }

    public Boolean getHasStandbyPower()
    {
        return hasStandbyPower;
    }

    public void setHasStandbyPower(Boolean hasStandbyPower)
    {
        this.hasStandbyPower = hasStandbyPower;
    }

    public String getWaterSupplySource()
    {
        return waterSupplySource;
    }

    public void setWaterSupplySource(String waterSupplySource)
    {
        this.waterSupplySource = waterSupplySource;
    }

    public String getWaterSupplyCapacity()
    {
        return waterSupplyCapacity;
    }

    public void setWaterSupplyCapacity(String waterSupplyCapacity)
    {
        this.waterSupplyCapacity = waterSupplyCapacity;
    }

    public String getSewageTreatmentCapacity()
    {
        return sewageTreatmentCapacity;
    }

    public void setSewageTreatmentCapacity(String sewageTreatmentCapacity)
    {
        this.sewageTreatmentCapacity = sewageTreatmentCapacity;
    }

    public Boolean getHasEmployeeDormitory()
    {
        return hasEmployeeDormitory;
    }

    public void setHasEmployeeDormitory(Boolean hasEmployeeDormitory)
    {
        this.hasEmployeeDormitory = hasEmployeeDormitory;
    }

    public String getSurroundingAmenities()
    {
        return surroundingAmenities;
    }

    public void setSurroundingAmenities(String surroundingAmenities)
    {
        this.surroundingAmenities = surroundingAmenities;
    }

    public String getLaborSupply()
    {
        return laborSupply;
    }

    public void setLaborSupply(String laborSupply)
    {
        this.laborSupply = laborSupply;
    }

    public String getRecruitmentSources()
    {
        return recruitmentSources;
    }

    public void setRecruitmentSources(String recruitmentSources)
    {
        this.recruitmentSources = recruitmentSources;
    }

    public Boolean getHasVocationalSchool()
    {
        return hasVocationalSchool;
    }

    public void setHasVocationalSchool(Boolean hasVocationalSchool)
    {
        this.hasVocationalSchool = hasVocationalSchool;
    }

    public String getMasterPlanUrl()
    {
        return masterPlanUrl;
    }

    public void setMasterPlanUrl(String masterPlanUrl)
    {
        this.masterPlanUrl = masterPlanUrl;
    }

    public String getBrochureUrl()
    {
        return brochureUrl;
    }

    public void setBrochureUrl(String brochureUrl)
    {
        this.brochureUrl = brochureUrl;
    }

    public List<String> getPhotos()
    {
        return photos;
    }

    public void setPhotos(List<String> photos)
    {
        this.photos = photos != null ? photos : new ArrayList<>();
    }

    public String getDataSource()
    {
        return dataSource;
    }

    public void setDataSource(String dataSource)
    {
        this.dataSource = dataSource;
    }

    public String getLastUpdated()
    {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated)
    {
        this.lastUpdated = lastUpdated;
    }

    public String getVerificationStatus()
    {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus)
    {
        this.verificationStatus = verificationStatus;
    }

    public String getConsultantContact()
    {
        return consultantContact;
    }

    public void setConsultantContact(String consultantContact)
    {
        this.consultantContact = consultantContact;
    }

    public String getContactPageUrl()
    {
        return contactPageUrl;
    }

    public void setContactPageUrl(String contactPageUrl)
    {
        this.contactPageUrl = contactPageUrl;
    }

    public String getOfficialPhone()
    {
        return officialPhone;
    }

    public void setOfficialPhone(String officialPhone)
    {
        this.officialPhone = officialPhone;
    }

    public String getOfficialEmail()
    {
        return officialEmail;
    }

    public void setOfficialEmail(String officialEmail)
    {
        this.officialEmail = officialEmail;
    }

    public String getOperatingHours()
    {
        return operatingHours;
    }

    public void setOperatingHours(String operatingHours)
    {
        this.operatingHours = operatingHours;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getSearchQuery()
    {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery)
    {
        this.searchQuery = searchQuery;
    }

    public String getDominantIndustry()
    {
        return dominantIndustry;
    }

    public void setDominantIndustry(String dominantIndustry)
    {
        this.dominantIndustry = dominantIndustry;
    }

    public BigDecimal getMaxLandRent()
    {
        return maxLandRent;
    }

    public void setMaxLandRent(BigDecimal maxLandRent)
    {
        this.maxLandRent = maxLandRent;
    }

    public BigDecimal getMinOccupancy()
    {
        return minOccupancy;
    }

    public void setMinOccupancy(BigDecimal minOccupancy)
    {
        this.minOccupancy = minOccupancy;
    }

    public BigDecimal getMaxDistancePort()
    {
        return maxDistancePort;
    }

    public void setMaxDistancePort(BigDecimal maxDistancePort)
    {
        this.maxDistancePort = maxDistancePort;
    }

    public Boolean getIsSezOnly()
    {
        return isSezOnly;
    }

    public void setIsSezOnly(Boolean isSezOnly)
    {
        this.isSezOnly = isSezOnly;
    }
}
