<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:common="http://lca.jrc.it/ILCD/Common"
                xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xs" version="1.0">

    <xsl:param name="ignoreReferencesToLCIAMethods" select="'false'"/>
    <xsl:param name="ignoreComplementingProcess" select="'false'"/>
    <xsl:param name="ignoreIncludedProcesses" select="'false'"/>
    <xsl:param name="ignorePrecedingDatasetVersion" select="'false'"/>

    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="/*">
        <xsl:element name="dataset">
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="*[local-name() = 'referenceToLCIAMethodDataSet' or local-name() = 'referenceToSupportedImpactAssessmentMethods']">
        <xsl:if test="string($ignoreReferencesToLCIAMethods) = 'false'">
            <xsl:call-template name="reference"/>
        </xsl:if>
    </xsl:template>

    <xsl:template match="*[local-name() = 'referenceToComplementingProcess']">
        <xsl:if test="string($ignoreComplementingProcess) = 'false'">
            <xsl:call-template name="reference"/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="*[local-name() = 'referenceToIncludedProcesses']">
        <xsl:if test="string($ignoreIncludedProcesses) = 'false'">
            <xsl:call-template name="reference"/>
        </xsl:if>
    </xsl:template>

    <xsl:template match="*[local-name() = 'referenceToPrecedingDataSetVersion']">
        <xsl:if test="string($ignorePrecedingDatasetVersion) = 'false'">
            <xsl:call-template name="reference"/>
        </xsl:if>
    </xsl:template>

    <xsl:template match = "*[local-name() = 'referenceToCommissioner'
            or local-name() = 'referenceToCompleteReviewReport'
            or local-name() = 'referenceToComplianceSystem'
            or local-name() = 'referenceToContact'
            or local-name() = 'referenceToConvertedOriginalDataSetFrom'
            or local-name() = 'referenceToDataHandlingPrinciples'
            or local-name() = 'referenceToDataSetFormat'
            or local-name() = 'referenceToDataSetUseApproval'
            or local-name() = 'referenceToDataSource'
            or local-name() = 'referenceToDiagram'
            or local-name() = 'referenceToEntitiesWithExclusiveAccess'
            or local-name() = 'referenceToExternalDocumentation'
            or local-name() = 'referenceToFlowDataSet'
            or local-name() = 'referenceToFlowPropertyDataSet'
            or local-name() = 'referenceToIncludedSubMethods'
            or local-name() = 'referenceToLCAMethodDetails'
            or local-name() = 'referenceToLCIAMethodFlowDiagrammOrPicture'
            or local-name() = 'referenceToLogo'
            or local-name() = 'referenceToNameOfReviewerAndInstitution'
            or local-name() = 'referenceToOriginalEPD'
            or local-name() = 'referenceToOwnershipOfDataSet'
            or local-name() = 'referenceToPersonOrEntityEnteringTheData'
            or local-name() = 'referenceToPersonOrEntityGeneratingTheDataSet'
            or local-name() = 'referenceToProcess'
            or local-name() = 'referenceToPublisher'
            or local-name() = 'referenceToRawDataDocumentation'
            or local-name() = 'referenceToReferenceUnitGroup'
            or local-name() = 'referenceToRegistrationAuthority'
            or local-name() = 'referenceToResultingProcess'
            or local-name() = 'referenceToSource'
            or local-name() = 'referenceToTechnicalSpecification'
            or local-name() = 'referenceToTechnologyFlowDiagrammOrPicture'
            or local-name() = 'referenceToTechnologyPictogramme'
            or local-name() = 'referenceToUnchangedRepublication'
            or local-name() = 'referenceToVendor'
            or local-name() = 'isA']">
        <xsl:call-template name="reference"/>
    </xsl:template>

    <xsl:template match="*[local-name() = 'referenceToDigitalFile']">
        <xsl:element name="reference">
            <xsl:attribute name="uri">
                <xsl:value-of select="@uri"/>
            </xsl:attribute>
            <xsl:attribute name="refObjectId">
                <xsl:value-of select="@refObjectId"/>
            </xsl:attribute>
            <xsl:attribute name="type">other external file</xsl:attribute>
            <xsl:attribute name="origin">
                <xsl:value-of select="local-name()"/>
            </xsl:attribute>
        </xsl:element>
    </xsl:template>

    <xsl:template name="reference">
        <xsl:element name="reference">
            <xsl:attribute name="uri">
                <xsl:value-of select="@uri"/>
            </xsl:attribute>
            <xsl:attribute name="refObjectId">
                <xsl:value-of select="@refObjectId"/>
            </xsl:attribute>
            <xsl:attribute name="type">
                <xsl:value-of select="@type"/>
            </xsl:attribute>
            <xsl:attribute name="origin">
                <xsl:value-of select="local-name()"/>
            </xsl:attribute>
            <xsl:attribute name="name">
                <xsl:choose>
                    <xsl:when test="common:shortDescription[@xml:lang='en']">
                        <xsl:value-of select="common:shortDescription[@xml:lang='en']/text()"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="common:shortDescription[1]/text()"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
        </xsl:element>
    </xsl:template>

    <xsl:template match="*">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="text()"/>

</xsl:stylesheet>
