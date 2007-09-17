def dao = daoService.getDao("DynaNode");

def createFolder(dao, path, label, cssClass) {
	def f = dao.getByPath(path)
	if(f) return f;
	
	f = dao.create("org.otherobjects.cms.model.SiteFolder");
	f.jcrPath=path
	f.label=label
	f.cssClass=cssClass
	f = dao.save(f)
	dao.publish(f)
	return f
}

def createDbFolder(dao, path, label, mainType) {
	def f = dao.getByPath(path)
	if(f) return f;
	
	f = dao.create("org.otherobjects.cms.model.DbFolder");
	f.jcrPath=path
	f.label=label
	f.mainType=mainType
	f = dao.save(f)
	dao.publish(f)
	return f
}

// Create base structure
createFolder(dao, "/smart", "\${smartFolders.label}", "smart-folders")
createFolder(dao, "/site", "\${site.label}", "site")
createFolder(dao, "/libraries", "\${libraries.label}", "libraries")
createFolder(dao, "/libraries/images", "\${libraries.images.label}", null)
createFolder(dao, "/libraries/files", "\${libraries.files.label}", null)
createFolder(dao, "/libraries/links", "\${libraries.links.label}", null)
def designer = createFolder(dao, "/designer", "\${designer.label}", "designer")
def security = createFolder(dao, "/security", "\${security.label}", "security")
createDbFolder(dao, "/security/users", "\${security.users.label}", "org.otherobjects.cms.model.User")
createDbFolder(dao, "/security/roles", "\${security.roles.label}", "org.otherobjects.cms.model.Role")
createFolder(dao, "/designer/blocks", "\${designer.blocks.label}", null)
createFolder(dao, "/designer/templates", "\${designer.templates.label}", null)
createFolder(dao, "/designer/layouts", "\${designer.layouts.label}", null)
createFolder(dao, "/trash", "\${trash.label}","trash")

// Move pre-created data types to proper location
def types = dao.getByPath("/types");
assert types : "Could not find types folder";
dao.moveItem(types.id, security.id, "below");
dao.publish(types);