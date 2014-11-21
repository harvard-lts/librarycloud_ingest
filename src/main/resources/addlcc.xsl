<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xs mods"
    xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:mods="http://www.loc.gov/mods/v3"
    xmlns:usage="http://lib.harvard.edu/usagedata" version="2.0">

    <xsl:output indent="no"/>
    <xsl:strip-space elements="*"/>

    <xsl:param name="param1"><lcc/></xsl:param>

    <xsl:template match="@*|node()">
        <xsl:copy-of select="."/>
    </xsl:template>

    <xsl:template match="mods:modsCollection">
        <xsl:copy>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="mods:mods">
        <xsl:copy>
            <xsl:apply-templates select="mods:titleInfo"/>
            <xsl:apply-templates select="mods:name"/>
            <xsl:apply-templates select="mods:typeOfResource"/>
            <xsl:apply-templates select="mods:genre"/>
            <xsl:apply-templates select="mods:originInfo"/>
            <xsl:apply-templates select="mods:language"/>
            <xsl:apply-templates select="mods:physicalDescription"/>
            <xsl:apply-templates select="mods:abstract"/>
            <xsl:apply-templates select="mods:tableOfContents"/>
            <xsl:apply-templates select="mods:targetAudience"/>
            <xsl:apply-templates select="mods:note"/>
            <xsl:apply-templates select="mods:subject"/>

            <xsl:variable name="lcc" select="$param1"/>
            <xsl:variable name="hollisid">
                <xsl:value-of select="./mods:recordInfo/mods:recordIdentifier"/>
            </xsl:variable>

            <xsl:for-each select="$lcc//docs[id_inst=$hollisid]">
                <xsl:if test="position()=1">
                    <xsl:if test="$lcc//docs[id_inst=$hollisid]/loc_call_num_subject">
                        <xsl:element name="subject" namespace="http://www.loc.gov/mods/v3">
                            <xsl:attribute name="authority"><xsl:text>LCC</xsl:text></xsl:attribute>
                            <xsl:element name="topic" namespace="http://www.loc.gov/mods/v3">
                                <xsl:value-of select="$lcc//docs[id_inst=$hollisid]/loc_call_num_subject"/>
                            </xsl:element>
                        </xsl:element>
                    </xsl:if>
                </xsl:if>
            </xsl:for-each>

            <xsl:apply-templates select="mods:classification"/>
            <xsl:apply-templates select="mods:relatedItem"/>
            <xsl:apply-templates select="mods:identifier"/>
            <xsl:apply-templates select="mods:location"/>
            <xsl:apply-templates select="accessCondition"/>
            <xsl:apply-templates select="mods:part"/>
            <xsl:apply-templates select="mods:extension"/>
            <xsl:apply-templates select="mods:recordInfo"/>

        </xsl:copy>
    </xsl:template>


</xsl:stylesheet>
