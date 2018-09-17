<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xlink="http://www.w3.org/TR/xlink" xmlns="http://www.loc.gov/mods/v3">

	<!--  Revisions
	  2015-03-13 fix key date (was ending up in component where it didn't belong
	  2015-03-13 put repository in a "type" attribute
 -->

	<xsl:output method="xml" omit-xml-declaration="yes" version="1.0" encoding="UTF-8" indent="yes"/>
	<!--<xsl:param name="urn">http://nrs.harvard.edu/urn-3:FHCL:1154698</xsl:param>-->
	<xsl:param name="urn"/>
  <xsl:param name="nodeComponentID" />
	<xsl:template match="/viaRecord">
		<xsl:message>URN: <xsl:value-of select="$urn"/></xsl:message>
		<xsl:message>SFX: <xsl:value-of select="$nodeComponentID"/></xsl:message>
		<mods xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xsi:schemaLocation="http://www.loc.gov/mods/v3 http://www.loc.gov/standards/mods/v3/mods-3-6.xsd">
			<xsl:apply-templates/>
			<xsl:variable name="urnsuffix">
				<xsl:choose>
					<xsl:when
						test="string-length($urn) and work/surrogate/image[contains(@href, $urn)]">
						<xsl:value-of
							select="work/surrogate/image[contains(@href, $urn)]/../@componentID"/>
					</xsl:when>
					<xsl:when
						test="string-length(work/surrogate[@componentID = $nodeComponentID]/image/@xlink:href)">
						<xsl:value-of
							select="substring-after(work/surrogate[@componentID = $nodeComponentID]/image/@xlink:href, 'edu/')"
						/>
					</xsl:when>
					<xsl:when test="string-length($urn)">
						<xsl:value-of select="substring-after($urn, 'edu/')"/>
					</xsl:when>
					<xsl:when test="string-length($nodeComponentID)">
						<xsl:value-of select="$nodeComponentID"/>
					</xsl:when>
					<xsl:otherwise> </xsl:otherwise>
				</xsl:choose>
			</xsl:variable>

			<relatedItem otherType="HOLLIS Images record">
				<location>
					<url>
						<xsl:text>http://id.lib.harvard.edu/images/</xsl:text>
						<xsl:value-of select="recordId"/>
						<xsl:text>/</xsl:text>
						<xsl:value-of select="$urnsuffix"/>
						<xsl:text>/catalog</xsl:text>
					</url>
				</location>
			</relatedItem>
			<recordInfo>
				<recordContentSource authority="marcorg">MH</recordContentSource>
				<recordContentSource authority="marcorg">MH-VIA</recordContentSource>
				<recordChangeDate encoding="iso8601">
					<xsl:variable name="lastdate">
						<xsl:choose>
							<xsl:when test="contains(admin/updateNote[position() = last()]/updateDate,' ')">
								<xsl:value-of select="substring-before(admin/updateNote[position() = last()]/updateDate/text(), ' ')"/>
							</xsl:when>
							<xsl:otherwise><xsl:value-of select="admin/updateNote[position() = last()]/updateDate/text()"/></xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:choose>
						<xsl:when test="contains($lastdate,'/')">
							<xsl:value-of select="replace($lastdate, '/', '')"/>							
						</xsl:when>
						<xsl:when test="contains($lastdate,'-')">
							<xsl:value-of select="replace($lastdate, '-', '')"/>							
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$lastdate"/>
						</xsl:otherwise>
					</xsl:choose>
				</recordChangeDate>
				<recordIdentifier>
					<xsl:attribute name="source">
						<xsl:value-of select="'MH:VIA'"/>
					</xsl:attribute>
					<xsl:choose>
						<xsl:when test="starts-with(recordId, 'olvwork')">
							<xsl:text>W</xsl:text>
							<xsl:value-of select="substring-after(recordId, 'olvwork')"/>
						</xsl:when>
						<xsl:when test="starts-with(recordId, 'olvgroup')">
							<xsl:text>G</xsl:text>
							<xsl:value-of select="substring-after(recordId, 'olvgroup')"/>
						</xsl:when>
						<xsl:when test="starts-with(recordId, 'olvsite')">
							<xsl:text>S</xsl:text>
							<xsl:value-of select="substring-after(recordId, 'olvsite')"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="recordId"/>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:if test="string-length($urnsuffix)">
						<xsl:text>_</xsl:text>
						<xsl:value-of select="$urnsuffix"/>
					</xsl:if>
				</recordIdentifier>
				<languageOfCataloging>
					<languageTerm>eng</languageTerm>
				</languageOfCataloging>
			</recordInfo>
			<language>
				<languageTerm type="code">zxx</languageTerm>
				<languageTerm type="text">No linguistic content</languageTerm>
			</language>
		</mods>
	</xsl:template>

	<xsl:template match="work">
		<xsl:call-template name="recordElements"/>
		<xsl:apply-templates select="surrogate"/>
	</xsl:template>

	<xsl:template match="group">
		<xsl:call-template name="recordElements"/>
		<xsl:apply-templates select="surrogate"/>
		<xsl:apply-templates select="subwork"/>
	</xsl:template>

	<xsl:template match="subwork">
		<xsl:choose>
			<xsl:when test="contains(image/@href, $urn) and string-length(image/@href)">
				<relatedItem type="constituent">
					<xsl:call-template name="recordElements"/>
					<recordInfo>
						<recordIdentifier>
							<xsl:value-of select="@componentID"/>
						</recordIdentifier>
					</recordInfo>
					<!--<xsl:apply-templates select="surrogate"/>-->
				</relatedItem>
			</xsl:when>
			<xsl:when test="contains(image/@xlink:href, $urn) and string-length(image/@xlink:href)">
				<relatedItem type="constituent">
					<xsl:call-template name="recordElements"/>
					<recordInfo>
						<recordIdentifier>
							<xsl:value-of select="@componentID"/>
						</recordIdentifier>
					</recordInfo>
					<!--<xsl:apply-templates select="surrogate"/>-->
				</relatedItem>
			</xsl:when>
			<xsl:when test="surrogate/@componentID = $nodeComponentID">
				<relatedItem type="constituent">
					<xsl:call-template name="recordElements"/>
					<recordInfo>
						<recordIdentifier>
							<xsl:value-of select="@componentID"/>
						</recordIdentifier>
					</recordInfo>
					<xsl:apply-templates select="surrogate"/>
				</relatedItem>
			</xsl:when>
			<xsl:otherwise/>
			<!--<xsl:otherwise>
				<xsl:apply-templates select="surrogate"/>
			</xsl:otherwise>-->
		</xsl:choose>
	</xsl:template>

	<xsl:template match="surrogate">
		<xsl:if
			test="(string-length($urn) and contains(image/@href, $urn)) or $nodeComponentID = @componentID">
			<relatedItem type="constituent">
				<xsl:call-template name="recordElements"/>
				<recordInfo>
					<recordIdentifier>
						<xsl:value-of select="@componentID"/>
					</recordIdentifier>
				</recordInfo>
			</relatedItem>
		</xsl:if>
	</xsl:template>

	<xsl:template name="recordElements">
		<xsl:apply-templates select="title"/>
		<xsl:apply-templates select="creator"/>
		<xsl:apply-templates select="associatedName"/>
		<xsl:call-template name="typeOfResource"/>
		<xsl:apply-templates select="workType"/>
		<xsl:call-template name="originInfo"/>
		<!--xsl:apply-templates select="production"/>
		<xsl:apply-templates select="structuredDate"/>
		<xsl:apply-templates select="freeDate"/>
		<xsl:apply-templates select="state"/-->
		<!--xsl:apply-templates select="physicalDescription"/>
		<xsl:apply-templates select="dimensions"/>
		<xsl:apply-templates select="workType"/-->
		<xsl:call-template name="physicalDescription"/>
		<xsl:apply-templates select="description"/>
		<xsl:apply-templates select="notes"/>
		<xsl:apply-templates select="placeName"/>
		<xsl:apply-templates select="topic"/>
		<xsl:apply-templates select="style"/>
		<xsl:apply-templates select="culture"/>
		<xsl:apply-templates select="materials"/>
		<xsl:apply-templates select="classification"/>
		<xsl:apply-templates select="relatedWork"/>
		<xsl:apply-templates select="relatedInformation"/>
		<xsl:apply-templates select="itemIdentifier"/>
		<xsl:apply-templates select="image"/>
		<xsl:apply-templates select="repository"/>
		<xsl:apply-templates select="location"/>
		<xsl:apply-templates select="useRestrictions"/>
		<xsl:apply-templates select="copyright"/>
	</xsl:template>

	<xsl:template match="title">
		<xsl:element name="titleInfo">
			<xsl:choose>
				<xsl:when test="(./type = 'Abbreviated Title')">
					<xsl:attribute name="type">
						<xsl:value-of select="'abbreviated'"/>
					</xsl:attribute>
				</xsl:when>
				<xsl:when test="(./type = 'Translated Title')">
					<xsl:attribute name="type">
						<xsl:value-of select="'translated'"/>
					</xsl:attribute>
				</xsl:when>
				<!--xsl:when test="(./type='Abbreviated Title' or ./type='Translated Title')">
				<xsl:attribute name="type">
					<xsl:value-of select="type"/>
				</xsl:attribute>
			</xsl:when-->
				<xsl:otherwise>
					<xsl:if test="./type">
						<xsl:attribute name="type">
							<xsl:value-of select="'alternative'"/>
						</xsl:attribute>
					</xsl:if>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:element name="title">
				<xsl:value-of select="normalize-space(textElement)"/>
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template match="creator">
		<xsl:call-template name="name">
			<xsl:with-param name="roleType">creator</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="associatedName">
		<xsl:call-template name="name">
			<xsl:with-param name="roleType">associated name</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="name">
		<xsl:param name="roleType"/>
		<name>
			<xsl:if test="nameElement">
				<namePart>
					<xsl:value-of select="nameElement"/>
				</namePart>
			</xsl:if>
			<xsl:if test="dates">
				<namePart type="date">
					<xsl:value-of select="dates"/>
				</namePart>
			</xsl:if>
			<xsl:if test="nationality">
				<namePart>
					<xsl:value-of select="nationality"/>
				</namePart>
			</xsl:if>
			<xsl:if test="place">
				<namePart>
					<xsl:value-of select="place"/>
				</namePart>
			</xsl:if>
			<role>
				<roleTerm>
					<xsl:value-of select="$roleType"/>
				</roleTerm>
			</role>
			<xsl:if test="role">
				<role>
					<roleTerm>
						<xsl:value-of select="role"/>
					</roleTerm>
				</role>
			</xsl:if>
		</name>
	</xsl:template>

	<xsl:template name="typeOfResource">
		<typeOfResource>still image</typeOfResource>
	</xsl:template>

	<xsl:template match="workType">
		<genre>
			<xsl:value-of select="."/>
		</genre>
	</xsl:template>

	<xsl:template name="originInfo">
		<xsl:variable name="wherefrom">
			<xsl:value-of select="name()"/>
		</xsl:variable>
		<xsl:if test="production | structuredDate | freeDate | state">
			<originInfo>
				<xsl:if test="production/placeOfProduction/place">
					<place>
						<placeTerm>
							<xsl:value-of select="production/placeOfProduction/place"/>
						</placeTerm>
					</place>
				</xsl:if>
				<xsl:if test="production/producer">
					<publisher>
						<xsl:value-of select="production/producer"/>
					</publisher>
				</xsl:if>
				<!-- dateOther keyDate is used for date sorting -->
				<!-- 2015-02-27 - but only for work/group-level-->
				<xsl:if test="$wherefrom = 'group' or $wherefrom = 'work'">
					<dateOther keyDate="yes">
						<xsl:if test="/record/metadata/viaRecord/@sortDate">
							<xsl:value-of select="/record/metadata/viaRecord/@sortDate"/>
						</xsl:if>
						<xsl:if test="not(/record/metadata/viaRecord/@sortDate)">
							<xsl:value-of select="freeDate[1]"/>
						</xsl:if>
					</dateOther>
				</xsl:if>
				<xsl:if test="structuredDate/beginDate">
					<dateCreated point="start">
						<xsl:value-of select="structuredDate/beginDate"/>
					</dateCreated>
				</xsl:if>
				<xsl:if test="structuredDate/beginDate">
					<dateCreated point="end">
						<xsl:value-of select="structuredDate/endDate"/>
					</dateCreated>
				</xsl:if>
				<xsl:if test="freeDate">
					<dateCreated>
						<xsl:value-of select="freeDate"/>
					</dateCreated>
				</xsl:if>
				<xsl:if test="state">
					<edition>
						<xsl:value-of select="state"/>
					</edition>
				</xsl:if>
			</originInfo>
		</xsl:if>
	</xsl:template>

	<xsl:template name="physicalDescription">
		<!--xsl:if test="physicalDescription or dimensions or workType"-->
		<xsl:if test="physicalDescription or dimensions">
			<physicalDescription>
				<xsl:if test="physicalDescription">
					<note>
						<xsl:value-of select="physicalDescription"/>
					</note>
				</xsl:if>
				<xsl:if test="dimensions">
					<extent>
						<xsl:value-of select="dimensions"/>
					</extent>
				</xsl:if>
				<!--xsl:if test="workType">
			<form>
				<xsl:value-of select="workType"/>
			</form>
		</xsl:if-->
			</physicalDescription>
		</xsl:if>
	</xsl:template>

	<!--xsl:template match="workType"></xsl:template>
<xsl:template match="physicalDescription"></xsl:template>
<xsl:template match="dimensions"></xsl:template-->

	<xsl:template match="description">
		<abstract>
			<xsl:value-of select="."/>
		</abstract>
	</xsl:template>

	<!-- 11MAY2006 mjv chng notes tmpl text: General now General Note -->

	<xsl:template match="notes">
		<note>
			<xsl:choose>
				<xsl:when test="starts-with(normalize-space(.), 'General:')">General note:
						<xsl:value-of select="substring-after(normalize-space(.), 'General:')"
					/></xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="."/>
				</xsl:otherwise>
			</xsl:choose>
		</note>
	</xsl:template>

	<!--xsl:template match="notes">
	<note>
		<xsl:value-of select="."/>
	</note>
</xsl:template-->

	<xsl:template match="placeName">
		<subject>
			<geographic>
				<xsl:value-of select="place"/>
			</geographic>
		</subject>
	</xsl:template>

	<xsl:template match="topic">
		<subject>
			<topic>
				<xsl:value-of select="term"/>
			</topic>
		</subject>
	</xsl:template>

	<xsl:template match="style">
		<extension
			xmlns:cdwalite="http://www.getty.edu/research/conducting_research/standards/cdwa/cdwalite">
			<cdwalite:styleWrap>
				<cdwalite:style>
					<xsl:value-of select="term"/>
				</cdwalite:style>
			</cdwalite:styleWrap>
		</extension>
		<!--subject>
		<topic>
			<xsl:value-of select="term"/>
		</topic>
	</subject-->
	</xsl:template>

	<xsl:template match="culture">
		<extension
			xmlns:cdwalite="http://www.getty.edu/research/conducting_research/standards/cdwa/cdwalite">
			<cdwalite:cultureWrap>
				<cdwalite:culture>
					<xsl:value-of select="term"/>
				</cdwalite:culture>
			</cdwalite:cultureWrap>
		</extension>
		<!--subject>
		<topic>
			<xsl:value-of select="term"/>
		</topic>
	</subject-->
	</xsl:template>

	<xsl:template match="materials">
		<extension
			xmlns:cdwalite="http://www.getty.edu/research/conducting_research/standards/cdwa/cdwalite">
			<cdwalite:indexingMaterialsTechSet>
				<cdwalite:termMaterialsTech>
					<xsl:value-of select="."/>
				</cdwalite:termMaterialsTech>
			</cdwalite:indexingMaterialsTechSet>
		</extension>
		<!--subject>
		<topic>
			<xsl:value-of select="."/>
		</topic>
	</subject-->
	</xsl:template>

	<xsl:template match="classification">
		<classification>
			<xsl:value-of select="number"/>
		</classification>
	</xsl:template>

	<xsl:template match="relatedWork">
		<relatedItem>
			<xsl:apply-templates select="relationship"/>
			<titleInfo>
				<title>
					<xsl:value-of select="textElement"/>
				</title>
			</titleInfo>
			<!--
		<extension xmlns:via="http://via.harvard.edu">
		    <via:relationship>
			<xsl:value-of select="relationship"/>
		    </via:relationship>
		</extension>
		-->
			<xsl:apply-templates select="creator"/>
			<xsl:call-template name="originInfo"/>
			<xsl:if test="contains(@href, $urn)">
				<location>
					<url>
						<xsl:value-of select="@href"/>
					</url>
				</location>
			</xsl:if>
		</relatedItem>
	</xsl:template>

	<xsl:template match="relationship">
		<xsl:attribute name="displayLabel">
			<xsl:value-of select="."/>
		</xsl:attribute>
	</xsl:template>

	<xsl:template match="relatedInformation">
		<relatedItem>
			<titleInfo>
				<title>
					<xsl:value-of select="."/>
				</title>
			</titleInfo>
			<location>
				<url>
					<xsl:value-of select="attribute::node()[local-name() = 'href']"/>
				</url>
			</location>
		</relatedItem>
	</xsl:template>

	<xsl:template match="itemIdentifier">
		<identifier>
			<xsl:attribute name="type">
				<xsl:value-of select="type"/>
			</xsl:attribute>
			<xsl:value-of select="number"/>
		</identifier>
	</xsl:template>

	<xsl:template match="image">
		<xsl:if test="string-length($urn) and contains(@href, $urn)">
			<xsl:choose>
				<xsl:when test="caption and not(../surrogate)">
					<relatedItem type="constituent">
						<titleInfo>
							<title>
								<xsl:value-of select="caption"/>
							</title>
						</titleInfo>
						<location>
							<url displayLabel="Full Image" access="raw object">
								<xsl:attribute name="note">
									<xsl:if test="./@restrictedImage = 'true'">
										<xsl:text>restricted</xsl:text>
									</xsl:if>
									<xsl:if test="./@restrictedImage = 'false'">
										<xsl:text>unrestricted</xsl:text>
									</xsl:if>
								</xsl:attribute>
								<xsl:value-of select="./@href"/>
							</url>
							<url displayLabel="Thumbnail" access="preview">
								<xsl:choose>
									<xsl:when test="contains(thumbnail/@href, 'width=')">
										<xsl:value-of select="thumbnail/@href"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="thumbnail/@href"/>
										<xsl:text>?width=150&amp;height=150&amp;usethumb=y</xsl:text>
									</xsl:otherwise>
								</xsl:choose>
							</url>
						</location>
					</relatedItem>
				</xsl:when>
				<xsl:otherwise>
					<location>
						<url displayLabel="Full Image" access="raw object">
							<xsl:attribute name="note">
								<xsl:if test="./@restrictedImage = 'true'">
									<xsl:text>restricted</xsl:text>
								</xsl:if>
								<xsl:if test="./@restrictedImage = 'false'">
									<xsl:text>unrestricted</xsl:text>
								</xsl:if>
							</xsl:attribute>
							<xsl:value-of select="./@href"/>
						</url>
						<url displayLabel="Thumbnail" access="preview">
							<xsl:choose>
								<xsl:when test="contains(thumbnail/@href, 'width=')">
									<xsl:value-of select="thumbnail/@href"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="thumbnail/@href"/>
									<xsl:text>?width=150&amp;height=150&amp;usethumb=y</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</url>
					</location>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
		<xsl:if test="contains(@xlink:href, $urn)">
			<xsl:choose>
				<xsl:when test="caption and not(../surrogate)">
					<relatedItem type="constituent">
						<titleInfo>
							<title>
								<xsl:value-of select="caption"/>
							</title>
						</titleInfo>
						<location>
							<url displayLabel="Full Image" access="raw object">
								<xsl:attribute name="note">
									<xsl:if test="./@restrictedImage = 'true'">
										<xsl:text>restricted</xsl:text>
									</xsl:if>
									<xsl:if test="./@restrictedImage = 'false'">
										<xsl:text>unrestricted</xsl:text>
									</xsl:if>
								</xsl:attribute>
								<xsl:value-of select="./@xlink:href"/>
							</url>
							<url displayLabel="Thumbnail" access="preview">
								<xsl:choose>
									<xsl:when
										test="contains(thumbnail/attribute::node()[local-name() = 'href'], 'width=')">
										<xsl:value-of
											select="thumbnail/attribute::node()[local-name() = 'href']"
										/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of
											select="thumbnail/attribute::node()[local-name() = 'href']"/>
										<xsl:text>?width=150&amp;height=150&amp;usethumb=y</xsl:text>
									</xsl:otherwise>
								</xsl:choose>
							</url>
						</location>
					</relatedItem>
				</xsl:when>
				<xsl:otherwise>
					<location>
						<url displayLabel="Full Image" access="raw object">
							<xsl:attribute name="note">
								<xsl:if test="./@restrictedImage = 'true'">
									<xsl:text>restricted</xsl:text>
								</xsl:if>
								<xsl:if test="./@restrictedImage = 'false'">
									<xsl:text>unrestricted</xsl:text>
								</xsl:if>
							</xsl:attribute>
							<xsl:value-of select="./@xlink:href"/>
						</url>
						<url displayLabel="Thumbnail" access="preview">
							<xsl:choose>
								<xsl:when
									test="contains(thumbnail/attribute::node()[local-name() = 'href'], 'width=')">
									<xsl:value-of
										select="thumbnail/attribute::node()[local-name() = 'href']"
									/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of
										select="thumbnail/attribute::node()[local-name() = 'href']"/>
									<xsl:text>?width=150&amp;height=150&amp;usethumb=y</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</url>
					</location>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<xsl:template match="repository">
		<location>
			<physicalLocation>
				<xsl:attribute name="type">
					<xsl:value-of select="'repository'"/>
				</xsl:attribute>
				<xsl:value-of select="repositoryName"/>
				<!-- 11 may 2006 mjv added number to end of repos name -->
				<!--
			<xsl:choose>
			  <xsl:when test="number">
			    <xsl:value-of select="repositoryName"/><xsl:text>; </xsl:text><xsl:value-of select="number"/>
			  </xsl:when>
			  <xsl:otherwise>
			    <xsl:value-of select="repositoryName"/>
			  </xsl:otherwise>
			</xsl:choose>
			-->
				<!-- do not display repository numbers -->
			</physicalLocation>
			<xsl:apply-templates select="number" mode="reposNo"/>
		</location>
	</xsl:template>

	<xsl:template match="number" mode="reposNo">
		<shelfLocator>
			<xsl:value-of select="."/>
		</shelfLocator>
	</xsl:template>

	<xsl:template match="location">
		<location>
			<physicalLocation>
				<xsl:attribute name="displayLabel">
					<xsl:value-of select="type"/>
				</xsl:attribute>
				<xsl:value-of select="place"/>
			</physicalLocation>
		</location>
	</xsl:template>

	<xsl:template match="useRestrictions">
		<accessCondition displayLabel="useRestrictions" type="restrictionOnAccess">
			<xsl:value-of select="."/>
		</accessCondition>
	</xsl:template>

	<xsl:template match="copyright">
		<accessCondition displayLabel="copyright" type="useAndReproduction">
			<xsl:value-of select="./@href"/>
			<xsl:value-of select="."/>
		</accessCondition>
	</xsl:template>

	<xsl:template match="admin"/>
	<xsl:template match="recordId"/>

</xsl:stylesheet>
