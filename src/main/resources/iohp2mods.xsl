<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xlink="http://www.w3.org/TR/xlink" xmlns="http://www.loc.gov/mods/v3"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.loc.gov/mods/v3 http://www.loc.gov/standards/mods/v3/mods-3-6.xsd">
    <xsl:output method="xml" omit-xml-declaration="yes" version="1.0" encoding="UTF-8" indent="yes"/>

    <xsl:template match="tedCollection">
        <xsl:element name="modsCollection">
            <xsl:copy-of select="document('')/*/@xsi:schemaLocation"/>
            <xsl:namespace name="xlink">
                <xsl:text>http://www.w3.org/1999/xlink</xsl:text>
            </xsl:namespace>
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="iohpRecord">
        <xsl:element name="mods">
            <xsl:copy-of select="document('')/*/@xsi:schemaLocation"/>
            <xsl:namespace name="xlink">
                <xsl:text>http://www.w3.org/1999/xlink</xsl:text>
            </xsl:namespace>
            <xsl:apply-templates select="tape"/>
            <xsl:apply-templates select="admin"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="tape">
        <xsl:apply-templates select="narrator"/>
        <xsl:apply-templates select="lifeStatus"/>
        <xsl:apply-templates select="biographicalNote"/>
        <xsl:apply-templates select="gender"/>
        <!--<xsl:apply-templates select="tapeNumber"/>-->
        <xsl:call-template name="makeTitle"/>
        <xsl:apply-templates select="digitalAudio"/>
        <!--<xsl:apply-templates select="digitalTranscript"/>-->
        <xsl:call-template name="makeDigitalTranscript"/>
        <!--<xsl:apply-templates select="pages"/>-->
        <!--<xsl:apply-templates select="tapeLength"/>-->
        <xsl:call-template name="makePhysDescForm"/>
        <xsl:apply-templates select="language"/>
        <xsl:apply-templates select="restriction"/>
        <xsl:apply-templates select="interviewer"/>
        <xsl:apply-templates select="interviewDate"/>
        <xsl:apply-templates select="interviewLocation"/>
        <xsl:apply-templates select="subject"/>
        <xsl:apply-templates select="transliteratedSubject"/>
        <xsl:apply-templates select="narratorSubject"/>
        <xsl:apply-templates select="narratorTransliterated"/>
        <xsl:apply-templates select="digitalAudioSuppressed"/>
        <xsl:apply-templates select="digitalTranscriptSuppressed"/>
        <xsl:apply-templates select="indexed"/>
    </xsl:template>

    <xsl:template match="narrator">
        <xsl:element name="name">
            <xsl:element name="namePart">
                <xsl:value-of select="."/>
            </xsl:element>
            <xsl:element name="role">
                <xsl:element name="roleTerm">
                    <xsl:attribute name="valueURI">
                        <xsl:text>http://id.loc.gov/vocabulary/relators/ive</xsl:text>
                    </xsl:attribute>
                    <xsl:text>Interviewee</xsl:text>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template match="lifeStatus | biographicalNote">
        <xsl:element name="note">
            <xsl:attribute name="type">biographical/historical</xsl:attribute>
            <xsl:value-of select="normalize-space(.)"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="gender">
        <xsl:element name="note">
            <xsl:attribute name="type">gender</xsl:attribute>
            <xsl:value-of select="normalize-space(.)"/>
        </xsl:element>
    </xsl:template>

    <!--<xsl:template match="tapeNumber">-->
    <xsl:template name="makeTitle">
        <xsl:element name="titleInfo">
            <xsl:element name="title">
                <xsl:text>Interview with </xsl:text>
                <xsl:value-of select="narrator"/>
                <xsl:text>: </xsl:text>
            </xsl:element>
            <xsl:element name="partNumber">
                <xsl:value-of select="tapeNumber"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template match="digitalAudio">
        <xsl:element name="relatedItem">
            <xsl:attribute name="type">
                <xsl:text>otherFormat</xsl:text>
            </xsl:attribute>
            <xsl:attribute name="displayLabel">
                <xsl:text>Digital Audio</xsl:text>
            </xsl:attribute>
            <xsl:element name="location">
                <xsl:element name="url">
                    <xsl:attribute name="access">
                        <xsl:text>raw object</xsl:text>
                    </xsl:attribute>
                    <xsl:value-of select="xlink:href"/>
                </xsl:element>
            </xsl:element>
            <xsl:element name="typeOfResource">
                <xsl:text>sound recording-nonmusical</xsl:text>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template name="makeDigitalTranscript">
        <xsl:element name="relatedItem">
            <xsl:attribute name="type">
                <xsl:text>otherVersion</xsl:text>
            </xsl:attribute>
            <xsl:attribute name="displayLabel">
                <xsl:text>Digital Transcript</xsl:text>
            </xsl:attribute>
            <xsl:apply-templates select="digitalTranscript"/>
            <xsl:element name="physicalDescription">
                <xsl:element name="extent">
                    <xsl:value-of select="pages"/>
                    <xsl:text> p.</xsl:text>
                </xsl:element>
                <!--<xsl:element name="extent">
                    <xsl:value-of select="tapeLength"/>
                </xsl:element>-->
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template match="digitalTranscript">
        <xsl:element name="location">
            <xsl:element name="url">
                <xsl:attribute name="access">
                    <xsl:text>raw object</xsl:text>
                </xsl:attribute>
                <xsl:value-of select="xlink:href"/>
            </xsl:element>
        </xsl:element>
        <xsl:element name="typeOfResource">
            <xsl:text>text</xsl:text>
        </xsl:element>
    </xsl:template>

    <xsl:template match="language">
        <xsl:element name="language">
            <xsl:element name="languageTerm">
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template name="makePhysDescForm">
        <xsl:element name="physicalDescription">
            <xsl:element name="form">
                <xsl:attribute name="valueURI">
                    <xsl:text>http://vocab.getty.edu/aat/300028661</xsl:text>
                </xsl:attribute>
                <xsl:text>audiocassettes</xsl:text>
            </xsl:element>
            <xsl:element name="extent">
                <xsl:value-of select="tapeLength"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template match="restriction">
        <xsl:element name="accessCondition">
            <xsl:attribute name="type">restriction on access</xsl:attribute>
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="interviewer">
        <xsl:element name="name">
            <xsl:element name="namePart">
                <xsl:value-of select="."/>
            </xsl:element>
            <xsl:element name="role">
                <xsl:element name="roleTerm">
                    <xsl:attribute name="valueURI">
                        <xsl:text>http://id.loc.gov/vocabulary/relators/ivr</xsl:text>
                    </xsl:attribute>
                    <xsl:text>Interviewer</xsl:text>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template match="subject | transliteratedSubject">
        <xsl:element name="subject">
            <xsl:element name="topic">
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template match="narratorSubject | narratorTransliterated">
        <xsl:element name="subject">
            <xsl:element name="name">
                <xsl:element name="namePart">
                    <xsl:value-of select="."/>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template match="admin">
        <xsl:element name="recordInfo">
            <xsl:apply-templates select="*"/>
            <xsl:element name="recordIdentifier">
                <xsl:attribute name="source">
                    <xsl:text>MH:IOHP</xsl:text>
                </xsl:attribute>
                <xsl:value-of select="lower-case(replace(replace(../_id, ' ', ''), ',', ''))"/>
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
