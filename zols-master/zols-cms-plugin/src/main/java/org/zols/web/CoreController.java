/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zols.datastore.service.SchemaService;
import org.zols.datatore.exception.DataStoreException;
import org.zols.templates.domain.PageRequest;
import org.zols.templates.service.PageService;

@Controller
public class CoreController {
    
    @Autowired
    private PageService pageService;
    
    @Autowired
    private SchemaService schemaService;

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/controlpanel")
    @Secured("ROLE_ADMIN")
    public String controlpanel() {
        return "zols/controlpanel";
    }

    @RequestMapping("/links")
    @Secured("ROLE_ADMIN")
    public String links() {
        return "zols/links";
    }

    @RequestMapping("/schema")
    @Secured("ROLE_ADMIN")
    public String schema() {
        return "zols/schema";
    }
    
    @RequestMapping("/master")
    @Secured("ROLE_ADMIN")
    public String master() throws DataStoreException {
        return "zols/master";
    }

    @RequestMapping("/templates")
    @Secured("ROLE_ADMIN")
    public String templates(Model model) throws DataStoreException {
        model.addAttribute("schemas", schemaService.list());
        return "zols/templates";
    }

    @RequestMapping("/pages/{name}")
    public String pages(@PathVariable(value = "name") String name,Model model) throws DataStoreException {
        PageRequest pageRequest = pageService.readRequest(name);
        model.addAttribute("data", pageRequest.getData());
        return pageRequest.getTemplate().getPath();
    }
    
    @RequestMapping("/create_page/{linkName}")
    @Secured("ROLE_ADMIN")
    public String createPage(@PathVariable(value = "linkName") String linkName,Model model) {
        model.addAttribute("linkName", linkName);
        return "zols/create_page";
    }

}
