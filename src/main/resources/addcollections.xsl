<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xs mods"
    xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:mods="http://www.loc.gov/mods/v3"
    xmlns:dcterms="http://purl.org/dc/terms/" xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:col="http://api.lib.harvard.edu/v2/collection/"
    xmlns:lc="http://hul.harvard.edu/ois/xml/ns/libraryCloud" version="1.0">

    <xsl:output encoding="UTF-8" indent="yes"/>
    <xsl:strip-space elements="*"/>

    <xsl:param name="param1"></xsl:param>

    <xsl:template match="@* | node()">
        <xsl:copy-of select="."/>
    </xsl:template>

    <xsl:template match="mods:modsCollection">
        <xsl:copy>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <!-- Remove existing collections -->
    <xsl:template match="mods:extension[lc:sets]"/>

    <xsl:template match="mods:mods">
        <xsl:copy>
            <xsl:apply-templates select="*[not(local-name() = 'recordInfo')]"/>
            <xsl:variable name="collections" select="$param1"/>
            <!--<xsl:variable name="holdings" select="document('')//xsl:param[@name='param1']//holdings"/>-->
            <xsl:variable name="recordid">
                <xsl:value-of select="./mods:recordInfo/mods:recordIdentifier"/>
            </xsl:variable>
            <xsl:element name="extension" namespace="http://www.loc.gov/mods/v3">
                <xsl:element name="sets"
                    namespace="http://hul.harvard.edu/ois/xml/ns/libraryCloud">
                     <xsl:for-each
                        select="$collections//col:item[col:item_id = $recordid]/col:collections">
                        <xsl:element name="set"
                            namespace="http://hul.harvard.edu/ois/xml/ns/libraryCloud">
                            <xsl:element name="systemId"
                                namespace="http://hul.harvard.edu/ois/xml/ns/libraryCloud">
                                <xsl:value-of select="col:systemId"/>
                            </xsl:element>
                            <xsl:element name="setName"
                                namespace="http://hul.harvard.edu/ois/xml/ns/libraryCloud">
                                <xsl:value-of select="col:setName"/>
                            </xsl:element>
                            <xsl:element name="setSpec"
                                namespace="http://hul.harvard.edu/ois/xml/ns/libraryCloud">
                                <xsl:value-of select="col:setSpec"/>
                            </xsl:element>
                            <xsl:element name="baseUrl"
                                namespace="http://hul.harvard.edu/ois/xml/ns/libraryCloud">
                              <xsl:value-of select="col:baseUrl"/>
                            </xsl:element>
                        </xsl:element>
                    </xsl:for-each>
                </xsl:element>
            </xsl:element>
            <xsl:apply-templates select="mods:recordInfo"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
