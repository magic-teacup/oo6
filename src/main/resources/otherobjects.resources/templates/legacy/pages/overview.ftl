<#assign pageTitle = "Welcome" />

<#include "/otherobjects/templates/legacy/blocks/header.ftl" />

<#include "/otherobjects/templates/legacy/blocks/nav-folders.ftl" />

<div class="oo-content">


<#--
<p>Drag this link: 
<a href="javascript:location.href='http://del.icio.us/post?v=4;url='+encodeURIComponent(location.href)+';title='+encodeURIComponent(document.title.replace(/^\s*|\s*$/g,''))" 
title="post to del.icio.us" onclick="window.alert('Drag this link to your bookmarks bar, or right-click it and choose Add Link to Bookmarks...');return false;"
class="bookmarklet">Add to OTHERobjects</a> up to your Bookmarks Toolbar.</p>
-->

<h2>My items in progress</h2>

<#-- freemarker automatically wraps results which means our PagedList isn't usable from freemarker -->
<#-- need to include current user in where criteria -->

<#assign edits = daoService.getDao("baseNode").pageByJcrExpression("/jcr:root/site//(*, oo:node) [@published = 'false' and not(jcr:like(@ooType,'%MetaData'))] order by @modificationTimestamp descending",10,1) >

<ul>
<#list edits.items as edit>
    <li class="published-false><a href="${edit.linkPath}">${edit.label} </a>
    <small>at ${edit.modificationTimestamp?date} </small></li>
</#list>
</ul>


<#--
<div class="welcome-block">
<h1>Bookmarklets</h1>
<p>Drag this link: 
<a href="javascript:location.href='http://localhost:8080/go/workbench/bookmarklet?v=1&url='+encodeURIComponent(location.href)+'&title='+encodeURIComponent(document.title.replace(/^\s*|\s*$/g,''))" 
title="post to del.icio.us" onclick="window.alert('Drag this link to your bookmarks bar, or right-click it and choose Add Link to Bookmarks...');return false;"
class="bookmarklet">Add to OTHERobjects</a> up to your Bookmarks Toolbar.</p>
</div>
-->

<h2>What happened recently</h2>
<p>Here are the last few changes to pages across the site:</p>
<#assign latestChanges = daoService.getDao("baseNode").pageByJcrExpression("/jcr:root/site//element(*, oo:node) [not(jcr:like(@ooType,'%MetaData'))] order by @modificationTimestamp descending",10,1) >
<ul>
<#list latestChanges.items as change>
    <li class="published-true><a href="${change.linkPath}">${change.label} </a> 
    <small>at ${change.modificationTimestamp?date} by ${change.userName}</small></li>
</#list>
</ul>

</div>

<#include "/otherobjects/templates/legacy/blocks/footer.ftl" />
