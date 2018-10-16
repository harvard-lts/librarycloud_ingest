<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:xlink="http://www.w3.org/TR/xlink"  
    xmlns:mods="http://www.loc.gov/mods/v3" 
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    exclude-result-prefixes="xsi"
    version="2.0">

<xsl:output method="xml" omit-xml-declaration="yes" version="1.0" encoding="UTF-8" indent="yes"/>
	<!--<xsl:param name="url">http://nrs.harvard.edu/urn-3:HBS.Baker.GEN:5028297-2011</xsl:param>-->
	<xsl:param name="url"></xsl:param>
	
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>	
	</xsl:template>
	
	<xsl:template match="mods:modsCollection">
		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="mods:mods">
		<xsl:copy>
			<xsl:apply-templates/>
			<!--<xsl:if test=".//mods:url[@access='raw object']=$url">
			<xsl:element name="relatedItem" namespace="http://www.loc.gov/mods/v3">
				<xsl:attribute name="otherType">HOLLIS record</xsl:attribute>
				<xsl:element name="location" namespace="http://www.loc.gov/mods/v3">
					<xsl:element name="url" namespace="http://www.loc.gov/mods/v3">
						<xsl:text>https://id.lib.harvard.edu/alma/</xsl:text>
						<xsl:value-of select="mods:recordInfo/mods:recordIdentifier"/>
						<xsl:text>/catalog</xsl:text>
					</xsl:element>
				</xsl:element>
			</xsl:element>
			</xsl:if>-->
		</xsl:copy>
	</xsl:template>
	
	<!--<xsl:template match="mods:mods">
		<xsl:choose>
			<xsl:when test=".//mods:url=$url">
				<xsl:copy>
					<xsl:apply-templates/>
				</xsl:copy>
			</xsl:when>
			<xsl:otherwise/>
		</xsl:choose>
	</xsl:template>-->
	
	<xsl:template match="mods:typeOfResource">
		<xsl:copy>
			<xsl:copy-of select="@*[not(name()='collection')]"/>
			<xsl:if test="$url=''">
				<xsl:attribute name="collection"><xsl:text>yes</xsl:text></xsl:attribute>
			</xsl:if>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="mods:recordIdentifier">
		<xsl:choose>
			<xsl:when test="../../mods:location/mods:url[@access='raw object']=$url">
				<xsl:copy>
					<xsl:copy-of select="@*"/>
					<xsl:variable name="urn">
						<xsl:choose>
							<xsl:when test="contains($url,'?')">
								<xsl:value-of select="substring-before(substring-after($url,'urn-3:'),'?')"/>                   
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="substring-after($url,'urn-3:')"/>                   
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:value-of select="."/><xsl:text>_</xsl:text><xsl:value-of select="$urn"/>
				</xsl:copy>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="."/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	
	<xsl:template match="mods:location[not(mods:url) and not(mods:physicalLocation/@type='repository')]">
		<xsl:choose>
			<xsl:when test="$url=''">
				<xsl:copy-of select="."/>
			</xsl:when>
			<xsl:otherwise/>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="mods:location[mods:physicalLocation/@type='repository']">
		<xsl:choose>
			<xsl:when test="$url=''">
				<xsl:copy-of select="."/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy>
					<!-- using matchedurl mode even tho not url, TO DO - change name -->
					<xsl:apply-templates select="mods:physicalLocation" mode="matchedurl"/>
					<xsl:apply-templates select="mods:shelfLocator" mode="matchedurl"/>
				</xsl:copy>
			</xsl:otherwise>

		</xsl:choose>
	</xsl:template>

	<xsl:template match="mods:location[mods:url]">
		<xsl:choose>
			<xsl:when test="./mods:url=$url">
				<xsl:copy>
					<xsl:apply-templates select="mods:physicalLocation" mode="matchedurl"/>
					<xsl:apply-templates select="mods:shelfLocator" mode="matchedurl"/>
					<xsl:apply-templates select="mods:url[.=$url]" mode="matchedurl"/>
					<xsl:apply-templates select="mods:holdingsSimple" mode="matchedurl"/>
					<xsl:apply-templates select="mods:holdingExternal" mode="matchedurl"/>
				</xsl:copy>
			</xsl:when>
			<xsl:when test="$url=''">
				<xsl:copy-of select="."/>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="*" mode="matchedurl">
		<xsl:copy-of select="."/>
	</xsl:template>
	
	<!--<xsl:template match="mods:location">
		<xsl:choose>
			<xsl:when test=""></xsl:when>
		</xsl:choose>
	</xsl:template>-->
	
	<!--<xsl:template match="mods:url[@access='raw object']">
		<xsl:choose>
			<xsl:when test=".=$url">
				<xsl:copy-of select="."/>
			</xsl:when>
			<xsl:when test="$url=''">
				<xsl:copy-of select="."/>
			</xsl:when>
		</xsl:choose>
	</xsl:template>-->
	
</xsl:stylesheet>
