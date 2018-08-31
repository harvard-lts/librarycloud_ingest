<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xlink="http://www.w3.org/TR/xlink" xmlns="http://www.loc.gov/mods/v3">
    <xsl:output method="xml" omit-xml-declaration="yes" version="1.0" encoding="UTF-8" indent="yes"/>

    <xsl:variable name="separator">
        <xsl:text>--</xsl:text>
    </xsl:variable>

    <xsl:template match="tedCollection">
        <xsl:element name="modsCollection">
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="mczRecord">
        <xsl:element name="mods">
            <xsl:apply-templates select="artwork"/>
            <xsl:apply-templates select="specimen"/>
            <xsl:apply-templates select="admin"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="artwork">
        <xsl:apply-templates select="physicalPieceId"/>
        <xsl:apply-templates select="image"/>
        <xsl:apply-templates select="title"/>
        <xsl:apply-templates select="imageCreator"/>
        <xsl:apply-templates select="workType"/>
        <xsl:call-template name="physicalDescripton"/>
        <xsl:call-template name="originInfoArtwork"/>
        <xsl:apply-templates select="expeditionName"/>
        <xsl:apply-templates select="expeditionLeader"/>
        <xsl:apply-templates select="annotation"/>
        <xsl:apply-templates select="note"/>
        <xsl:apply-templates select="publishedIn"/>
        <xsl:call-template name="repository"/>
    </xsl:template>

    <xsl:template match="specimen">
        <xsl:element name="relatedItem">
            <xsl:attribute name="otherType">Specimen</xsl:attribute>
            <xsl:apply-templates select="taxonomicClassification"/>
            <xsl:apply-templates select="classificationNote"/>
            <xsl:apply-templates select="commonName"/>
            <xsl:apply-templates select="originalName"/>
            <xsl:apply-templates select="authority"/>
            <xsl:apply-templates select="specimenCollector"/>
            <!--<xsl:apply-templates select="specimenCollectionDate"/>
            <xsl:apply-templates select="locationCollected"/>-->
            <xsl:call-template name="originInfoSpecimen"/>
            <xsl:apply-templates select="specimenNote"/>
        </xsl:element>
    </xsl:template>

    <!-- artwork templates -->
    <xsl:template match="physicalPieceId">
        <xsl:element name="identifier">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="image">
        <xsl:element name="location">
            <xsl:element name="url">
                <xsl:attribute name="access">
                    <xsl:text>raw object</xsl:text>
                </xsl:attribute>
                <xsl:value-of select="xlink:href"/>
            </xsl:element>
            <xsl:apply-templates select="thumbnail"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="thumbnail">
        <xsl:element name="url">
            <xsl:attribute name="access">preview</xsl:attribute>
            <xsl:value-of select="xlink:href"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="title">
        <xsl:element name="titleInfo">
            <xsl:element name="title">
                <xsl:value-of select="normalize-space(.)"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template match="imageCreator">
        <xsl:element name="name">
            <xsl:element name="namePart">
                <xsl:value-of select="normalize-space(.)"/>
            </xsl:element>
            <xsl:element name="role">
                <xsl:element name="roleTerm">
                    <xsl:attribute name="type">text</xsl:attribute>
                    <xsl:text>Creator</xsl:text>
                </xsl:element>
            </xsl:element>
            <xsl:element name="role">
                <xsl:element name="roleTerm">
                    <xsl:attribute name="type">text</xsl:attribute>
                    <xsl:text>Artist</xsl:text>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template match="workType">
        <xsl:element name="genre">
            <xsl:value-of select="normalize-space(.)"/>
        </xsl:element>
    </xsl:template>

    <xsl:template name="physicalDescripton">
        <xsl:element name="physicalDescription">
            <xsl:apply-templates select="materialsTechniques"/>
            <xsl:apply-templates select="support"/>
            <xsl:apply-templates select="dimensions"/>
            <xsl:apply-templates select="imageDescription"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="materialsTechniques">
        <xsl:element name="form">
            <xsl:attribute name="type">materialsTechniques</xsl:attribute>
            <xsl:value-of select="normalize-space(.)"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="support">
        <xsl:element name="form">
            <xsl:attribute name="type">support</xsl:attribute>
            <xsl:value-of select="normalize-space(.)"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="dimensions">
        <xsl:element name="extent">
            <xsl:value-of select="normalize-space(.)"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="imageDescription">
        <xsl:element name="note">
            <xsl:value-of select="normalize-space(.)"/>
        </xsl:element>
    </xsl:template>

    <xsl:template name="originInfoArtwork">
        <xsl:if test="imageCreationDate or imageCreationLocation">
            <xsl:element name="originInfo">
                <xsl:apply-templates select="imageCreationDate"/>
                <xsl:apply-templates select="imageCreationLocation"/>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template match="imageCreationDate">
        <xsl:element name="dateCreated">
            <xsl:value-of select="normalize-space(.)"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="imageCreationLocation">
        <xsl:element name="place">
            <xsl:element name="placeTerm">
                <xsl:attribute name="type">text</xsl:attribute>
                <xsl:value-of select="normalize-space(.)"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template match="expeditionName">
        <xsl:element name="name">
            <xsl:attribute name="displayLabel">Expedition Name</xsl:attribute>
            <xsl:value-of select="normalize-space(.)"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="expeditionLeader">
        <xsl:element name="name">
            <xsl:attribute name="displayLabel">Expedition Leader</xsl:attribute>
            <xsl:value-of select="normalize-space(.)"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="annotation">
        <xsl:apply-templates select="text" mode="annotation"/>
        <xsl:apply-templates select="annotatedBy"/>
    </xsl:template>

    <xsl:template match="text" mode="annotation">
        <xsl:element name="note">
            <xsl:attribute name="type">annotation</xsl:attribute>
            <xsl:value-of select="normalize-space(.)"/>
            <xsl:if test="../annotatedBy">
                <xsl:value-of select="$separator"/>
                <xsl:value-of select="../annotatedBy"/>
            </xsl:if>
        </xsl:element>
    </xsl:template>

    <xsl:template match="annotatedBy">
        <xsl:element name="name">
            <xsl:element name="namePart">
                <xsl:value-of select="normalize-space(.)"/>
            </xsl:element>
            <xsl:element name="role">
                <xsl:element name="roleTerm">
                    <xsl:attribute name="type">text</xsl:attribute>
                    <xsl:attribute name="valueURI">
                        <xsl:text>http://id.loc.gov/vocabulary/relators/ann</xsl:text>
                    </xsl:attribute>
                    <xsl:text>Annotator</xsl:text>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template match="note | publishedIn">
        <xsl:element name="note">
            <xsl:value-of select="normalize-space(.)"/>
        </xsl:element>
    </xsl:template>

    <xsl:template name="repository">
        <xsl:element name="location">
            <xsl:element name="physicalLocation">
                <xsl:attribute name="type">repository</xsl:attribute>
                <xsl:text>Ernst Mayr Library</xsl:text>
                <xsl:apply-templates select="otherItemIdentifier"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template match="otherItemIdentifier">
        <xsl:apply-templates select="IdValue"/>
    </xsl:template>

    <xsl:template match="IdValue">
        <xsl:element name="shelfLocator">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <!-- specimen templates -->

    <xsl:template match="taxonomicClassification">
        <xsl:element name="subject">
            <xsl:attribute name="displayLabel">Taxonomic Classification</xsl:attribute>
            <xsl:element name="topic">
                <xsl:value-of select="kingdom"/>
                <xsl:apply-templates select="phylum"/>
                <xsl:apply-templates select="class"/>
                <xsl:apply-templates select="order"/>
                <xsl:apply-templates select="family"/>
                <xsl:apply-templates select="genus"/>
                <xsl:apply-templates select="species"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template match="phylum | class | order | family | genus | species">
        <xsl:value-of select="$separator"/>
        <xsl:value-of select="normalize-space(.)"/>
    </xsl:template>

    <xsl:template match="classificationNote">
        <xsl:element name="note">
            <xsl:attribute name="type">Classification Note</xsl:attribute>
            <xsl:value-of select="normalize-space(.)"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="commonName">
        <xsl:element name="titleInfo">
            <xsl:attribute name="type">Common Name</xsl:attribute>
            <xsl:element name="title">
                <xsl:value-of select="normalize-space(.)"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template match="originalName">
        <xsl:element name="titleInfo">
            <xsl:attribute name="type">Original Name</xsl:attribute>
            <xsl:element name="title">
                <xsl:value-of select="normalize-space(.)"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template match="authority">
        <xsl:element name="name">
            <xsl:element name="namePart">
                <xsl:value-of select="normalize-space(.)"/>
            </xsl:element>
            <xsl:element name="role">
                <xsl:element name="roleTerm">
                    <xsl:text>Authority</xsl:text>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template match="specimenCollector">
        <xsl:element name="name">
            <xsl:attribute name="displayLabel">Specimen Collector</xsl:attribute>
            <xsl:element name="namePart">
                <xsl:value-of select="normalize-space(.)"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template name="originInfoSpecimen">
        <xsl:if test="specimenCollectionDate or locationCollected">
            <xsl:element name="originInfo">
                <xsl:attribute name="displayLabel">Specimen Collected</xsl:attribute>
                <xsl:apply-templates select="specimenCollectionDate"/>
                <xsl:apply-templates select="locationCollected"/>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template match="specimenCollectionDate">
        <xsl:element name="dateOther">
            <xsl:attribute name="type">Specimen Collection Date</xsl:attribute>
            <xsl:value-of select="normalize-space(.)"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="locationCollected">
        
        <xsl:element name="place">
            <xsl:element name="placeTerm">
                <xsl:attribute name="type">text</xsl:attribute>
                <xsl:value-of select="normalize-space(.)"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template match="specimenNote">
        <xsl:element name="note">
            <xsl:attribute name="type">Specimen Note</xsl:attribute>
            <xsl:value-of select="normalize-space(.)"/>
        </xsl:element>
    </xsl:template>

    <!-- admin templates -->

    <xsl:template match="admin">
        <xsl:element name="recordInfo">
            <xsl:apply-templates/>
            <xsl:element name="recordIdentifier">
                <xsl:attribute name="source">
                    <xsl:text>MH:TED</xsl:text>
                </xsl:attribute>
                <xsl:value-of select="../_id"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template match="createDate">
        <xsl:element name="recordCreationDate">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="updateNote">
        <xsl:element name="recordInfoNote">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="updateDate">
        <xsl:element name="recordChangeDate">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="*"/>
</xsl:stylesheet>
