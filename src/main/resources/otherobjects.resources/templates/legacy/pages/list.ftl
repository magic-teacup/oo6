<#import "/spring.ftl" as spring />
<#import "/oo.ftl" as oo />

<#macro renderProperty prop object>
<tr>
<td class="oo-label">${prop.label}</td>
<td class="oo-field-none">
	<#if object.getPropertyValue(prop.propertyPath)?? >
		<#if prop.type == "component" >
			${object.getPropertyValue(prop.propertyPath)!}
		<#elseif prop.type == "date" >
			
			${object.getPropertyValue(prop.propertyPath)?string("d MMM yyyy")}
		<#elseif prop.type == "boolean" >
			${object.getPropertyValue(prop.propertyPath)?string("Yes", "No")}	
	  	<#else>
			${object.getPropertyValue(prop.propertyPath)!}
	  	</#if>
  	<#else>
  		No value
  	</#if>  	
</td>
</tr>
</#macro>

<#assign object = daoService.getDao("baseNode").get(id) />

<#assign pageTitle = "Listing: ${oo.msg(folder.label)}" />

<#include "/otherobjects/templates/legacy/blocks/header.ftl" />

<#include "/otherobjects/templates/legacy/blocks/nav-folders.ftl" />

<div class="oo-content">
<h2>
${pageTitle}
</h2>


<table class="oo-listing">
<thead>
<tr>
<th>Name</th>
<th>Type</th>
<th>Action</th>
<th>Action</th>
</tr>
</thead>
<tbody>
<#list items as item>
<#if ! item.class.name?ends_with("Folder")> <!--FIXME -->
 <tr>
	<td><a class="oo-<#if item.published?? && list.published>live<#else>edited</#if>" href="${oo.url('/otherobjects/workbench/view/${item.id}')}">${item.label!}</a></td>
	<#if item.linkPath??>
		<td title="${item.typeDef.name}"><p>${item.typeDef.label}</p></td>
	<#else>
		<td title="${item.class.name}"><p>${item.class.name}</p></td>	
	</#if>
	<td class="oo-action"><#if item.linkPath??><a href="${oo.url(item.linkPath)}">Preview</a></#if></td>
	<td class="oo-action"><a href="${oo.url('/otherobjects/workbench/edit/${item.id}')}">Edit</a></td>
</tr>
</#if>
</#list>
</tbody>
</table>
</div>



<div class="oo-actions">
<h2>Actions</h2>

<ul>
<li><a href="${oo.url('/otherobjects/workbench/view/'+folder.id)}">View folder details ...</a></li>
<li><a href="${oo.url('/otherobjects/workbench/create/org.otherobjects.cms.model.SiteFolder?container='+folder.id)}">New sub folder ...</a></li>
<#list folder.allAllowedTypes as type>
<#if type?exists>
<li class="divider"><a href="${oo.url('/otherobjects/workbench/create/${type.name}?container=${folder.id}')}">New ${type.label} ...</a></li>
</#if>
</#list>
</ul>
</div>

<#include "/otherobjects/templates/legacy/blocks/footer.ftl" />

