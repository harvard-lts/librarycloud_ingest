<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.loc.gov/mods/v3"
    xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.loc.gov/mods/v3 http://www.loc.gov/standards/mods/v3/mods-3-4.xsd"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    version="2.0">
    <xsl:output encoding="UTF-8" method="xml" indent="yes" omit-xml-declaration="yes"/>
    <xsl:strip-space elements="*"/>
    <!--<xsl:param name="componentid">div00579c00002</xsl:param>-->
    <xsl:param name="componentid"/>

    <xsl:variable name="cid_legacy_or_new">
  <xsl:choose>
      <xsl:when test="//c[@id=$componentid]">
    <xsl:value-of select="$componentid"/>
      </xsl:when>
      <xsl:when test="//c[@id=substring($componentid,9)]">
    <xsl:value-of select="substring($componentid,9)"/>
      </xsl:when>
  </xsl:choose>
    </xsl:variable>


    <xsl:template match="ead">
        <xsl:variable name="sibcount">
            <xsl:apply-templates select="//c[@id=$cid_legacy_or_new]" mode="siblingcount"/>
        </xsl:variable>
        <mods xmlns:xlink="http://www.w3.org/1999/xlink">
            <xsl:apply-templates select="//c[@id = $cid_legacy_or_new]/did/unittitle[not(. = '')]"/>
            <xsl:apply-templates select="//c[@id = $cid_legacy_or_new]/did//unitdate[not(. = '')]"/>
            <xsl:apply-templates select="did//origination"/>
            <!--<xsl:apply-templates select="//c[@id=$componentid]/did//persname"/>
            <xsl:apply-templates select="//c[@id=$componentid]/did//famname"/>
            <xsl:apply-templates select="//c[@id=$componentid]/did//corpname"/>-->
            <xsl:apply-templates select="//c[@id=$cid_legacy_or_new]/did//physdesc"/>
            <xsl:apply-templates select="//c[@id=$cid_legacy_or_new]/@level"/>
            <xsl:apply-templates select="//c[@id=$cid_legacy_or_new]/did//unitid"/>
            <xsl:apply-templates select="//c[@id=$cid_legacy_or_new]/did//container"/>
            <xsl:apply-templates select="//c[@id=$cid_legacy_or_new]/did//language[string-length(@langcode) and string-length(text())]"/>

            <xsl:if test="count(//c[@id=$cid_legacy_or_new]/did//language[string-length(@langcode) and string-length(text())]) &lt; 1">
              <xsl:element name="language">
                <xsl:element name="languageTerm">
                  <xsl:attribute name="authority">iso639-2b</xsl:attribute>
                  <xsl:attribute name="type">code</xsl:attribute>
                  <xsl:text>und</xsl:text>
                </xsl:element>
                <xsl:element name="languageTerm">
                  <xsl:attribute name="authority">iso639-2b</xsl:attribute>
                  <xsl:attribute name="type">text</xsl:attribute>
                  <xsl:text>Undefined</xsl:text>
                </xsl:element>
              </xsl:element>
            </xsl:if>

            <!--<xsl:apply-templates select="//c[@id=$cid_legacy_or_new]//accessrestrict"/>-->
            <!--<xsl:apply-templates select="(//c[@id=$cid_legacy_or_new and not(.//accessrestrict) and ancestor::*//accessrestrict]/ancestor::*//accessrestrict)[position()=1]"/>-->
            <xsl:choose>
                <xsl:when test="//c[@id=$cid_legacy_or_new]/accessrestrict">
                    <xsl:apply-templates select="//c[@id=$cid_legacy_or_new]/accessrestrict"/>
                </xsl:when>
                <xsl:when test="//c[@id=$cid_legacy_or_new and ancestor::c/accessrestrict]">
                    <xsl:apply-templates select="//c[@id=$cid_legacy_or_new]/ancestor::c[accessrestrict][position()=1]/accessrestrict"/>
                </xsl:when>
                <xsl:when test="//archdesc[accessrestrict]">
                   <xsl:apply-templates select="//archdesc/accessrestrict"/>
                </xsl:when>
            </xsl:choose>


            <!--<xsl:call-template name="access">
                <xsl:with-param name="cid">
                    <xsl:value-of select="//c[@id=$cid_legacy_or_new]"/>
                </xsl:with-param>
            </xsl:call-template>-->
            <xsl:apply-templates select="//c[@id=$cid_legacy_or_new]/scopecontent//p[1]"/>
            <xsl:apply-templates select="//c[@id=$cid_legacy_or_new]/dao"/>
            <xsl:apply-templates select="//c[@id=$cid_legacy_or_new]/daogrp"/>
            <xsl:apply-templates select="//c[@id=$cid_legacy_or_new]/did/dao"/>
            <xsl:apply-templates select="//c[@id=$cid_legacy_or_new]/did/daogrp"/>
            <xsl:element name="recordInfo">
                <xsl:if test="eadheader/revisiondesc/change[item='Loaded into OASIS']">
                    <xsl:element name="recordChangeDate">
                      <xsl:attribute name="encoding">iso8601</xsl:attribute>
                        <xsl:value-of select="format-number(max(eadheader/revisiondesc/change[item='Loaded into OASIS']/date/@normal),'00000000')"/>
                    </xsl:element>
                </xsl:if>
                <xsl:element name="recordIdentifier">
                    <xsl:attribute name="id"><xsl:text>s</xsl:text><xsl:value-of select="$sibcount"/></xsl:attribute>
                    <xsl:attribute name="source">MH:OASIS</xsl:attribute>
                    <xsl:value-of select="//c[@id=$cid_legacy_or_new]/@id"/>
                </xsl:element>
            </xsl:element>
            <xsl:apply-templates select="//c[@id=$cid_legacy_or_new]"/>
            <!-- <xsl:apply-templates select="/ead/eadheader/profiledesc/langusage" /> -->
        </mods>
    </xsl:template>

    <xsl:template match="c" mode="siblingcount">
        <xsl:value-of select="count(preceding-sibling::c) + 1"/>
    </xsl:template>

    <xsl:template match="c">
        <relatedItem>
            <xsl:attribute name="type">host</xsl:attribute>
            <xsl:if test="parent::c">
                <xsl:apply-templates select="parent::c/did/unittitle"/>
                <xsl:apply-templates select="parent::c/did//unitdate"/>
                 <xsl:apply-templates select="parent::c/did//unitid"/>
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
                            <xsl:text>http://id.lib.harvard.edu/aleph/</xsl:text>
                            <xsl:value-of select="/ead/eadheader/eadid/@identifier"/>
                            <xsl:text>/catalog</xsl:text>
                        </url>
                    </location>
                </relatedItem>
                <relatedItem otherType="Finding Aid">
                    <location>
                        <url>
                            <xsl:text>http://id.lib.harvard.edu/ead/</xsl:text>
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
                <xsl:element name="dateCreated">
                    <xsl:value-of select="@normal"/>
                </xsl:element>
            </xsl:if>
        </xsl:element>
    </xsl:template>
    <xsl:template match="persname|famname|corpname">
        <xsl:element name="name">
            <xsl:element name="namePart">
                <xsl:value-of select="text()"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template match="origination">
        <xsl:element name="name">
            <xsl:element name="namePart">
                <xsl:value-of select="normalize-space(.)"/>
            </xsl:element>
            <xsl:element name="role">
                <xsl:element name="roleTerm">
                    <xsl:text>originator</xsl:text>
                </xsl:element>
            </xsl:element>
            <xsl:element name="role">
                <xsl:element name="roleTerm">
                    <xsl:text>creator</xsl:text>
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
                <xsl:attribute name="type"><xsl:text>organization</xsl:text></xsl:attribute>
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
            <xsl:attribute name="type"><xsl:text>container</xsl:text></xsl:attribute>
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
                <xsl:attribute name="type"><xsl:text>repository</xsl:text></xsl:attribute>
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
            <xsl:value-of select="@langcode" />
          </xsl:element>
          <xsl:element name="languageTerm">
            <xsl:attribute name="authority">iso639-2b</xsl:attribute>
            <xsl:attribute name="type">text</xsl:attribute>
            <xsl:value-of select="text()" />
          </xsl:element>
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
            <xsl:apply-templates select="daoloc[@xlink:label=../arc/@xlink:to[../@xlink:show='embed']]"/>
            <xsl:apply-templates select="daoloc[@xlink:label=../arc/@xlink:to[../@xlink:show='new']]"/>
        <!--<xsl:element name="location">
            <xsl:element name="url">
                <xsl:value-of select="daoloc/@href"/>
            </xsl:element>
        </xsl:element>-->
        </xsl:element>
    </xsl:template>

    <xsl:template match="daoloc[@xlink:label=../arc/@xlink:to[../@xlink:show='embed']]">
        <xsl:element name="url">
            <xsl:attribute name="access">preview</xsl:attribute>
            <xsl:attribute name="displayLabel">Thumbnail</xsl:attribute>
            <xsl:if test="@href"><xsl:value-of select="@href"/></xsl:if>
            <xsl:if test="@xlink:href"><xsl:value-of select="@xlink:href"/></xsl:if>
        </xsl:element>
    </xsl:template>
    <xsl:template match="daoloc[@xlink:label=../arc/@xlink:to[../@xlink:show='new']]">
        <xsl:element name="url">
            <xsl:attribute name="access">raw object</xsl:attribute>
            <xsl:attribute name="displayLabel">Full Image</xsl:attribute>
            <xsl:if test="@href"><xsl:value-of select="@href"/></xsl:if>
            <xsl:if test="@xlink:href"><xsl:value-of select="@xlink:href"/></xsl:if>
        </xsl:element>
    </xsl:template>

    <xsl:template match="langusage">
      <xsl:for-each select="language[string-length(@langcode) and string-length(text())]">
        <xsl:element name="language">
          <xsl:element name="languageTerm">
            <xsl:attribute name="authority">iso639-2b</xsl:attribute>
            <xsl:attribute name="type">code</xsl:attribute>
            <xsl:value-of select="@langcode" />
          </xsl:element>
          <xsl:element name="languageTerm">
            <xsl:attribute name="authority">iso639-2b</xsl:attribute>
            <xsl:attribute name="type">text</xsl:attribute>
            <xsl:value-of select="text()" />
          </xsl:element>
        </xsl:element>
      </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>
