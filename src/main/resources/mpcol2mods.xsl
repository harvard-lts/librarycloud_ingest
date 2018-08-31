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

    <xsl:template match="mpcolRecord">
        <xsl:element name="mods">
            <xsl:apply-templates select="record"/>
            <xsl:apply-templates select="admin"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="record">
        <xsl:apply-templates select="singer"/>
        <xsl:apply-templates select="title"/>
        <xsl:apply-templates select="translatedTitle"/>
        <xsl:apply-templates select="alternateTitle"/>
        <xsl:apply-templates select="location"/>
        <xsl:call-template name="originInfo"/>
        <xsl:apply-templates select="genre"/>
        <xsl:apply-templates select="subject"/>
        <xsl:apply-templates select="language"/>
        <xsl:apply-templates select="collectionMethod"/>
        <xsl:apply-templates select="collection"/>
        <xsl:apply-templates select="itemNumber"/>
        <xsl:apply-templates select="audio"/>
        <xsl:apply-templates select="text"/>
    </xsl:template>

    <xsl:template match="singer">
        <xsl:apply-templates select="name"/>
        <xsl:apply-templates select="biographicalNote"/>
    </xsl:template>

    <xsl:template match="name">
        <xsl:element name="name">
            <xsl:element name="namePart">
                <xsl:value-of select="normalize-space(.)"/>
            </xsl:element>
            <xsl:element name="role">
                <xsl:attribute name="type">text</xsl:attribute>
                <xsl:attribute name="valueURI">
                    <xsl:text>http://id.loc.gov/vocabulary/relators/sn</xsl:text>
                </xsl:attribute>
                <xsl:text>Singer</xsl:text>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template match="biographicalNote">
        <xsl:element name="note">
            <xsl:attribute name="type">biographical/historical</xsl:attribute>
            <xsl:value-of select="normalize-space(../name)"/>
            <xsl:text>: </xsl:text>
            <xsl:value-of select="normalize-space(.)"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="title | translatedTitle | alternateTitle">
        <xsl:element name="titleInfo">
            <xsl:choose>
                <xsl:when test="name() = 'translatedTitle'">
                    <xsl:attribute name="type">translated</xsl:attribute>
                </xsl:when>
                <xsl:when test="name() = 'alternateTitle'">
                    <xsl:attribute name="type">alternative</xsl:attribute>
                </xsl:when>
                <xsl:otherwise/>
            </xsl:choose>
            <xsl:element name="title">
                <xsl:value-of select="normalize-space(.)"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template name="originInfo">
        <xsl:element name="originInfo">
            <xsl:apply-templates select="date"/>
            <xsl:apply-templates select="dateRange"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="date">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="singleDate">
        <xsl:element name="dateOther">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="dateType">
        <xsl:attribute name="type">
            <xsl:value-of select="."/>
        </xsl:attribute>
    </xsl:template>

    <xsl:template match="dateRange">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="startDate">
        <xsl:element name="dateOther">
            <xsl:attribute name="point">start</xsl:attribute>
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="endDate">
        <xsl:element name="dateOther">
            <xsl:attribute name="point">end</xsl:attribute>
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="genre">
        <xsl:element name="genre">
            <xsl:value-of select="normalize-space(.)"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="genre">
        <xsl:element name="subject">
            <xsl:element name="topic">
                <xsl:value-of select="normalize-space(.)"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template match="language">
        <xsl:element name="language">
            <xsl:element name="languageTerm">
                <xsl:value-of select="normalize-space(.)"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template match="collectionMethod">
        <xsl:element name="note">
            <xsl:attribute name="type">Collection Method</xsl:attribute>
            <xsl:value-of select="normalize-space(.)"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="collection">
        <xsl:element name="relatedItem">
            <xsl:attribute name="otherType">collection</xsl:attribute>
            <xsl:element name="titleInfo">
                <xsl:element name="title">
                    <xsl:value-of select="normalize-space(.)"/>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template match="itemNumber">
        <xsl:element name="identifier">
            <xsl:attribute name="displayLabel">Item Number</xsl:attribute>
            <xsl:value-of select="normalize-space(.)"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="audio">
        <xsl:element name="relatedItem">
            <xsl:attribute name="displayLabel">Audio</xsl:attribute>
            <xsl:apply-templates select="recordingNumber"/>
            <xsl:apply-templates select="digitalAudio"/>
            <xsl:call-template name="audioPhysicalDescription"/>
            <xsl:apply-templates select="audioNote"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="recordingNumber">
        <xsl:element name="identifier">
            <xsl:attribute name="displayLabel">Recording Number</xsl:attribute>
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="digitalAudio">
        <xsl:element name="location">
            <xsl:element name="url">
                <xsl:value-of select="normalize-space(xlink:href)"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template name="audioPhysicalDescription">
        <xsl:element name="physicalDescription">
            <xsl:apply-templates select="playingTime"/>
            <xsl:apply-templates select="physicalMedium"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="playingTime">
        <xsl:element name="extent">
            <xsl:value-of select="normalize-space(.)"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="physicalMedium">
        <xsl:element name="form">
            <xsl:value-of select="normalize-space(.)"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="audioNote">
        <xsl:element name="note">
            <xsl:value-of select="normalize-space(.)"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="text">
        <xsl:element name="relatedItem">
            <xsl:attribute name="type">otherVersion</xsl:attribute>
            <xsl:attribute name="displayLabel">Text</xsl:attribute>
            <xsl:value-of select="availableText"/>
            <xsl:apply-templates select="transcriber"/>
            <xsl:apply-templates select="dictatedTo"/>
            <xsl:apply-templates select="numberOfLines"/>
            <xsl:apply-templates select="digitalText"/>
            <xsl:apply-templates select="publishedIn"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="transcriber">
        <xsl:element name="name">
            <xsl:element name="namePart">
                <xsl:value-of select="normalize-space(.)"/>
            </xsl:element>
            <xsl:element name="role">
                <xsl:element name="roleTerm">
                    <xsl:attribute name="type">text</xsl:attribute>
                    <xsl:attribute name="valueURI">
                        <xsl:text>http://id.loc.gov/vocabulary/relators/trc</xsl:text>
                    </xsl:attribute>
                    <xsl:text>Transcriber</xsl:text>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template match="dictatedTo">
        <xsl:element name="name">
            <xsl:element name="namePart">
                <xsl:value-of select="normalize-space(.)"/>
            </xsl:element>
            <xsl:element name="role">
                <xsl:element name="roleTerm">
                    <xsl:attribute name="type">text</xsl:attribute>
                    <xsl:text>Dictated To</xsl:text>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template match="numberOfLines">
        <xsl:element name="physicalDescription">
            <xsl:element name="extent">
                <xsl:attribute name="unit">lines</xsl:attribute>
                <xsl:value-of select="normalize-space(.)"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template match="digitalText">
        <xsl:element name="location">
            <xsl:element name="url">
                <xsl:value-of select="normalize-space(xlink:href)"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template match="publishedIn">
        <xsl:element name="note">
            <xsl:attribute name="type">publications</xsl:attribute>
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
