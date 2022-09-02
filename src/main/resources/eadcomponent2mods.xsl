<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.loc.gov/mods/v3"
    xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:librarycloud="http://hul.harvard.edu/ois/xml/ns/librarycloud" xmlns:roles="urn:roles"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.loc.gov/mods/v3 http://www.loc.gov/standards/mods/v3/mods-3-4.xsd"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xpath-default-namespace="urn:isbn:1-931666-22-9"
    version="2.0">
    <xsl:output encoding="UTF-8" method="xml" indent="yes" omit-xml-declaration="yes"/>
    <xsl:strip-space elements="*"/>
    <xsl:param name="componentid"></xsl:param>
    <!-- comment above, uncomment below for
    testing (you can change the id -->
    <!--<xsl:param name="componentid">hou00001c00002</xsl:param>-->
    <xsl:key name="roletextlookup" match="roles:map" use="roles:code"/>
    <xsl:variable name="originationroles"
        select="document('src/main/resources/originationroles.xml')/roles:originationroles"/>
    <!-- comment above, uncomment below for local testing with oxygen -->
    <!--<xsl:variable name="originationroles"
        select="document('originationroles.xml')/roles:originationroles"/>-->
    <xsl:variable name="cid_legacy_or_new">
        <xsl:choose>
            <xsl:when test="//c[@id = $componentid]">
                <xsl:value-of select="$componentid"/>
            </xsl:when>
            <xsl:when test="//c[@id = substring($componentid, 9)]">
                <xsl:value-of select="substring($componentid, 9)"/>
            </xsl:when>
        </xsl:choose>
    </xsl:variable>
    <xsl:template match="ead">
        <xsl:variable name="cmatch">
            <xsl:copy-of select="archdesc/dsc//c[@id = $cid_legacy_or_new]"/>
        </xsl:variable>
        <mods xmlns:xlink="http://www.w3.org/1999/xlink">
            <xsl:apply-templates select="$cmatch/c/did/unittitle[not(. = '')]"/>
            <xsl:apply-templates select="$cmatch/c/did//unitdate[not(. = '')]"/>
            <xsl:apply-templates select="did//origination"/>
            <xsl:apply-templates select="$cmatch/c/did//physdesc"/>
            <xsl:apply-templates select="$cmatch/c/@level"/>
            <xsl:apply-templates select="$cmatch/c/did//unitid"/>
            <xsl:apply-templates select="$cmatch/c/did//container"/>
            <xsl:apply-templates select="$cmatch/c/did//origination"/>
            <xsl:apply-templates select="$cmatch/c/otherfindaid[head = 'HOLLIS record']"/>
            <xsl:apply-templates select="$cmatch/c/controlaccess/genreform"/>
            <xsl:apply-templates select="$cmatch/c/odd"/>
            <xsl:apply-templates select="$cmatch/c/prefercite[head = 'Preferred Citation']"/>
            <xsl:apply-templates select="$cmatch/c/userestrict"/>
            <xsl:apply-templates
                select="$cmatch/c/did//language[string-length(@langcode) and string-length(text())]"/>
            <xsl:apply-templates
                select="$cmatch/c/altformavail[head = 'Existence and Location of Copies']"/>
            <xsl:choose>
                <xsl:when test="$cmatch/c/altformavail[head = 'Digitization Funding']">
                    <xsl:apply-templates
                        select="$cmatch/c/altformavail[head = 'Digitization Funding']"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates
                        select="/ead/archdesc/altformavail[head = 'Digitization Funding']"/>
                </xsl:otherwise>
            </xsl:choose>
            <!--<xsl:if
                test="count($cmatch/c/did//language[string-length(@langcode) and string-length(text())]) &lt; 1">
                <xsl:element name="language">
                    <xsl:element name="languageTerm">
                        <xsl:attribute name="authority">iso639-2b</xsl:attribute>
                        <xsl:attribute name="type">code</xsl:attribute>
                        <xsl:value-of select="@langcode"
                    </xsl:element>
                    <xsl:element name="languageTerm">
                        <xsl:attribute name="authority">iso639-2b</xsl:attribute>
                        <xsl:attribute name="type">text</xsl:attribute>
                        <xsl:value-of select="."/>
                    </xsl:element>
                </xsl:element>
            </xsl:if>-->
            <xsl:choose>
                <xsl:when test="$cmatch/c/accessrestrict">
                    <xsl:apply-templates select="$cmatch/c/accessrestrict"/>
                </xsl:when>
                <xsl:when test="$cmatch/c[ancestor::c/accessrestrict]">
                    <xsl:apply-templates
                        select="$cmatch/c/ancestor::c[accessrestrict][position() = 1]/accessrestrict"
                    />
                </xsl:when>
                <xsl:when test="archdesc[accessrestrict]">
                    <xsl:apply-templates select="archdesc/accessrestrict"/>
                </xsl:when>
            </xsl:choose>
            <xsl:apply-templates select="$cmatch/c/scopecontent//p[1]"/>
            <xsl:apply-templates select="$cmatch/c/bioghist"/>
            <xsl:apply-templates select="$cmatch/c/dao"/>
            <xsl:apply-templates select="$cmatch/c/daogrp"/>
            <xsl:apply-templates select="$cmatch/c/did/dao"/>
            <xsl:apply-templates select="$cmatch/c/did/daogrp"/>
            <relatedItem otherType="HOLLIS for Archival Discovery record">
                <location>
                    <url>
                        <xsl:text>https://id.lib.harvard.edu/ead/c/</xsl:text>
                        <xsl:value-of select="$cmatch/c/@id"/>
                        <xsl:text>/catalog</xsl:text>
                    </url>
                </location>
            </relatedItem>
            <xsl:element name="recordInfo">
                <xsl:element name="recordChangeDate">
                    <xsl:attribute name="encoding">iso8601</xsl:attribute>
                    <xsl:value-of
                        select="replace(substring-before(eadheader/profiledesc/creation/date, ' '), '-', '')"
                    />
                </xsl:element>
                <xsl:element name="recordIdentifier">
                    <xsl:attribute name="source">MH:OASIS</xsl:attribute>
                    <xsl:value-of select="$cmatch/c/@id"/>
                </xsl:element>
            </xsl:element>
            <extension xmlns="http://www.loc.gov/mods/v3">
                <librarycloud:originalDocument>
                    <xsl:text>https://s3.amazonaws.com/harvard.ead/</xsl:text>
                    <xsl:value-of select="eadheader/eadid"/>
                    <xsl:text>.xml</xsl:text>
                </librarycloud:originalDocument>
            </extension>
            <xsl:apply-templates select="//c[@id = $cid_legacy_or_new]"/>
        </mods>
    </xsl:template>
    <xsl:template match="c">
        <relatedItem>
            <xsl:attribute name="type">host</xsl:attribute>
            <xsl:if test="parent::c">
                <xsl:apply-templates select="parent::c/did/unittitle"/>
                <xsl:apply-templates select="parent::c/did//unitdate"/>
                <xsl:apply-templates select="parent::c/did//unitid"/>
                <xsl:apply-templates select="parent::c/bioghist"/>
                <xsl:apply-templates select="parent::c/did/origination"/>
                <xsl:apply-templates select="parent::c/otherfindaid[head = 'HOLLIS record']"/>
                <xsl:apply-templates select="parent::c/controlaccess/genreform"/>
                <xsl:apply-templates select="parent::c/odd"/>
                <xsl:apply-templates select="parent::c/prefercite[head = 'Preferred Citation']"/>
                <xsl:apply-templates select="parent::c/userestrict"/>
                <xsl:apply-templates
                    select="parent::c/altformavail[head = 'Existence and Location of Copies']"/>
                <xsl:apply-templates
                    select="parent::c/did//language[string-length(@langcode) and string-length(text())]"/>
                <xsl:element name="recordInfo">
                    <xsl:element name="recordIdentifier">
                        <xsl:value-of select="parent::c/@id"/>
                    </xsl:element>
                </xsl:element>
                <xsl:apply-templates select="parent::c"/>
            </xsl:if>
            <xsl:if test="not(parent::c)">
                <xsl:attribute name="displayLabel">collection</xsl:attribute>
                <xsl:apply-templates select="/ead/archdesc/did//repository"/>
                <xsl:apply-templates select="/ead/archdesc/did//unitid"/>
                <xsl:apply-templates select="/ead/archdesc/did/origination"/>
                <xsl:apply-templates select="/ead/archdesc/did/unittitle"/>
                <xsl:apply-templates select="/ead/archdesc/did//unitdate"/>
                <xsl:element name="recordInfo">
                    <xsl:element name="recordIdentifier">
                        <xsl:value-of select="/ead/eadheader/eadid"/>
                    </xsl:element>
                </xsl:element>
                <relatedItem otherType="HOLLIS record">
                    <location>
                        <url>
                            <xsl:text>https://id.lib.harvard.edu/alma/</xsl:text>
                            <xsl:value-of select="/ead/eadheader/eadid/@identifier"/>
                            <xsl:text>/catalog</xsl:text>
                        </url>
                    </location>
                </relatedItem>
                <relatedItem otherType="Finding Aid">
                    <location>
                        <url>
                            <xsl:text>https://id.lib.harvard.edu/ead/</xsl:text>
                            <xsl:value-of select="/ead/eadheader/eadid"/>
                            <xsl:text>/catalog</xsl:text>
                        </url>
                    </location>
                </relatedItem>
            </xsl:if>
        </relatedItem>
    </xsl:template>
    <xsl:template match="unittitle">
        <xsl:element name="titleInfo">
            <xsl:element name="title">
                <xsl:value-of select="normalize-space(.)"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="unitdate">
        <xsl:element name="originInfo">
            <xsl:if test="string-length(@normal)">
                <xsl:choose>
                    <xsl:when test='matches(@normal, "\d{4}-\d{2}-\d{2}/\d{4}-\d{2}-\d{2}")'>
                        <xsl:element name="dateCreated">
                            <xsl:attribute name="point">start</xsl:attribute>
                            <xsl:value-of select="substring-before(@normal, '/')"/>
                        </xsl:element>
                        <xsl:element name="dateCreated">
                            <xsl:attribute name="point">end</xsl:attribute>
                            <xsl:value-of select="substring-after(@normal, '/')"/>
                        </xsl:element>
                    </xsl:when>
                    <xsl:when test='matches(@normal, "\d{4}-\d{2}/\d{4}-\d{2}")'>
                        <xsl:element name="dateCreated">
                            <xsl:attribute name="point">start</xsl:attribute>
                            <xsl:value-of select="substring-before(@normal, '/')"/>
                        </xsl:element>
                        <xsl:element name="dateCreated">
                            <xsl:attribute name="point">end</xsl:attribute>
                            <xsl:value-of select="substring-after(@normal, '/')"/>
                        </xsl:element>
                    </xsl:when>
                    <xsl:when test='matches(@normal, "\d{4}/\d{4}")'>
                        <xsl:element name="dateCreated">
                            <xsl:attribute name="point">start</xsl:attribute>
                            <xsl:value-of select="substring-before(@normal, '/')"/>
                        </xsl:element>
                        <xsl:element name="dateCreated">
                            <xsl:attribute name="point">end</xsl:attribute>
                            <xsl:value-of select="substring-after(@normal, '/')"/>
                        </xsl:element>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:element name="dateCreated">
                            <xsl:value-of select="@normal"/>
                        </xsl:element>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:element name="dateCreated">
                <xsl:attribute name="keyDate">yes</xsl:attribute>
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="persname | famname | corpname">
        <xsl:element name="name">
            <xsl:element name="namePart">
                <xsl:value-of select="text()"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="origination">
        <xsl:apply-templates select="persname | famname | corpname | name" mode="originationNames"/>
    </xsl:template>
    <xsl:template match="persname | famname | corpname | name" mode="originationNames">
        <xsl:choose>
            <xsl:when test="./@role = 'pbl'">
                <xsl:element name="originInfo">
                    <xsl:element name="publisher">
                        <xsl:value-of select="normalize-space(.)"/>
                    </xsl:element>
                </xsl:element>
            </xsl:when>
            <!--<xsl:when test="string-length(./@role) &gt; 3">
                <xsl:element name="name">
                    <xsl:element name="namePart">
                        <xsl:value-of select="normalize-space(.)"/>
                    </xsl:element>
                    <xsl:element name="role">
                        <xsl:element name="roleTerm">
                            <xsl:value-of select="@role"/>
                        </xsl:element>
                    </xsl:element>
                </xsl:element>
            </xsl:when>-->
            <xsl:otherwise>
                <xsl:apply-templates select="$originationroles">
                    <xsl:with-param name="originationName" select="."/>
                </xsl:apply-templates>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="roles:originationroles">
        <xsl:param name="originationName"/>
        <xsl:variable name="rolecode" select="$originationName/@role"/>
        <xsl:element name="name">
            <xsl:element name="namePart">
                <xsl:value-of select="normalize-space($originationName)"/>
            </xsl:element>
            <xsl:element name="role">
                <xsl:element name="roleTerm">
                    <xsl:choose>
                        <xsl:when test="key('roletextlookup', $rolecode)/roles:fulltext != ''">
                            <xsl:value-of select="key('roletextlookup', $rolecode)/roles:fulltext"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$rolecode"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="physdesc">
        <xsl:element name="physicalDescription">
            <xsl:element name="extent">
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="extent">
        <xsl:element name="physicalDescription">
            <xsl:element name="extent">
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="@level">
        <xsl:element name="physicalDescription">
            <xsl:element name="note">
                <xsl:attribute name="type">
                    <xsl:text>organization</xsl:text>
                </xsl:attribute>
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="unitid">
        <xsl:element name="identifier">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="container">
        <xsl:element name="location">
            <xsl:element name="physicalLocation">
                <xsl:attribute name="type">
                    <xsl:text>container</xsl:text>
                </xsl:attribute>
                <xsl:if test="@type">
                    <xsl:value-of select="@type"/><xsl:text> </xsl:text>
                </xsl:if>
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="scopecontent//p[1]">
        <xsl:element name="abstract">
            <xsl:value-of select="normalize-space(.)"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="repository">
        <xsl:element name="location">
            <xsl:element name="physicalLocation">
                <xsl:attribute name="type">
                    <xsl:text>repository</xsl:text>
                </xsl:attribute>
                <xsl:value-of select="normalize-space()"/>
                <!--<xsl:if test="./*">
                    <xsl:value-of select="./*"/>
                </xsl:if>-->
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="accessrestrict">
        <xsl:element name="accessCondition">
            <xsl:value-of select="text()"/>
            <xsl:if test="p">
                <xsl:value-of select="p"/>
            </xsl:if>
        </xsl:element>
    </xsl:template>
    <xsl:template match="language">
        <xsl:element name="language">
            <xsl:element name="languageTerm">
                <xsl:attribute name="authority">iso639-2b</xsl:attribute>
                <xsl:attribute name="type">code</xsl:attribute>
                <xsl:value-of select="@langcode"/>
            </xsl:element>
            <xsl:element name="languageTerm">
                <xsl:attribute name="authority">iso639-2b</xsl:attribute>
                <xsl:attribute name="type">text</xsl:attribute>
                <xsl:value-of select="text()"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="bioghist">
        <xsl:element name="note">
            <xsl:attribute name="type">bibliographic history</xsl:attribute>
            <xsl:attribute name="displayLabeo">Issuing Body Note</xsl:attribute>
            <xsl:if test="p">
                <xsl:value-of select="normalize-space(p[1])"/>
            </xsl:if>
        </xsl:element>
    </xsl:template>
    <!--
    <xsl:template name="access">
        <xsl:param name="cid"/>
        <xsl:choose>
            <xsl:when test="c[@cid]//accessrestrict">
                <xsl:element name="accessCondition">
                    <xsl:value-of select="c[@cid]//accessrestrict"/>
                </xsl:element>
            </xsl:when>
            <xsl:when test="c[@cid][parent::accessrestrict]">
                <xsl:element name="accessCondition">
                    <xsl:value-of select="c[@cid][parent::accessrestrict[1]]"/>
                </xsl:element>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    -->
    <xsl:template match="dao">
        <xsl:element name="location">
            <xsl:element name="url">
                <xsl:attribute name="access">raw object</xsl:attribute>
                <xsl:value-of select="@*[local-name() = 'href']"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="daogrp">
        <xsl:element name="location">
            <xsl:apply-templates
                select="daoloc[@*[local-name() = 'label'] = ../arc/@*[local-name() = 'to'][../@*[local-name() = 'show'] = 'embed']]"/>
            <xsl:apply-templates
                select="daoloc[@*[local-name() = 'label'] = ../arc/@*[local-name() = 'to'][../@*[local-name() = 'show'] = 'new']]"/>
            <!--<xsl:element name="location">
            <xsl:element name="url">
                <xsl:value-of select="daoloc/@href"/>
            </xsl:element>
        </xsl:element>-->
        </xsl:element>
    </xsl:template>
    <xsl:template
        match="daoloc[@*[local-name() = 'label'] = ../arc/@*[local-name() = 'to'][../@*[local-name() = 'show'] = 'embed']]">
        <xsl:element name="url">
            <xsl:attribute name="access">preview</xsl:attribute>
            <xsl:attribute name="displayLabel">Thumbnail</xsl:attribute>
            <xsl:value-of select="@*[local-name() = 'href']"/>
        </xsl:element>
    </xsl:template>
    <xsl:template
        match="daoloc[@*[local-name() = 'label'] = ../arc/@*[local-name() = 'to'][../@*[local-name() = 'show'] = 'new']]">
        <xsl:element name="url">
            <xsl:attribute name="access">raw object</xsl:attribute>
            <xsl:attribute name="displayLabel">Full Image</xsl:attribute>
            <xsl:value-of select="@*[local-name() = 'href']"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="langusage">
        <xsl:for-each select="language[string-length(@langcode) and string-length(text())]">
            <xsl:element name="language">
                <xsl:element name="languageTerm">
                    <xsl:attribute name="authority">iso639-2b</xsl:attribute>
                    <xsl:attribute name="type">code</xsl:attribute>
                    <xsl:value-of select="@langcode"/>
                </xsl:element>
                <xsl:element name="languageTerm">
                    <xsl:attribute name="authority">iso639-2b</xsl:attribute>
                    <xsl:attribute name="type">text</xsl:attribute>
                    <xsl:value-of select="text()"/>
                </xsl:element>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>
    <xsl:template match="altformavail">
        <xsl:if test="./head = 'Digitization Funding'">
            <xsl:element name="note">
                <xsl:attribute name="type">funding</xsl:attribute>
                <xsl:value-of select="p"/>
            </xsl:element>
        </xsl:if>
    </xsl:template>
    <xsl:template match="otherfindaid">
        <xsl:if test="p">
            <xsl:element name="relatedItem">
                <xsl:attribute name="otherType">HOLLIS record</xsl:attribute>
                <xsl:element name="location">
                    <xsl:element name="url">
                        <xsl:value-of select="normalize-space(p)"/>
                    </xsl:element>
                </xsl:element>
            </xsl:element>
        </xsl:if>
    </xsl:template>
    <xsl:template match="controlaccess/genreform">
        <xsl:element name="genre">
            <xsl:value-of select="normalize-space(.)"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="odd">
        <xsl:choose>
            <xsl:when test="head = 'Physical Description of Original'">
                <xsl:element name="note">
                    <xsl:attribute name="type">source characteristics</xsl:attribute>
                    <xsl:attribute name="displayLabel">
                        <xsl:text>Physical Description of Original</xsl:text>
                    </xsl:attribute>
                    <xsl:value-of select="normalize-space(p)"/>
                </xsl:element>
            </xsl:when>
            <xsl:when test="head = 'Place of Publication'">
                <xsl:element name="originInfo">
                    <xsl:element name="place">
                        <xsl:element name="placeTerm">
                            <xsl:value-of select="normalize-space(p)"/>
                        </xsl:element>
                    </xsl:element>
                </xsl:element>
            </xsl:when>
            <xsl:otherwise/>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="prefercite[head = 'Preferred Citation']">
        <xsl:element name="note">
            <xsl:attribute name="type">preferred citation</xsl:attribute>
            <xsl:value-of select="normalize-space(p)"/>
        </xsl:element>
    </xsl:template>   
    <xsl:template match="userestrict">
        <xsl:element name="accessCondition">
            <xsl:attribute name="type">use and reproduction</xsl:attribute>
            <xsl:value-of select="normalize-space(p)"/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="altformavail[head = 'Existence and Location of Copies']">
        <xsl:element name="relatedItem">
            <xsl:attribute name="othertype">Source Institution Digitization</xsl:attribute>
            <xsl:attribute name="displayLabel">Source Institution Digitization</xsl:attribute>
            <xsl:element name="titleInfo">
                <xsl:element name="title">
                    <xsl:value-of select="normalize-space(p/extref)"/>
                </xsl:element>
            </xsl:element>
            <xsl:element name="location">
                <xsl:element name="url">
                    <xsl:value-of select="p/extref/@*[local-name() = 'href']"/>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>
</xsl:stylesheet>
