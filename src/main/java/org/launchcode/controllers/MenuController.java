package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.management.modelmbean.ModelMBeanOperationInfo;
import javax.validation.Valid;

@Controller
@RequestMapping("menu")
public class MenuController {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CheeseDao cheeseDao;


    @RequestMapping(value="")
    public String index(Model model){

        model.addAttribute("menus",menuDao.findAll() );
        model.addAttribute("title", "Categories");
        return "menu/index";
    }
    @RequestMapping(value="add", method = RequestMethod.GET)
    public String displayAddMenuForm(Model model){
        model.addAttribute("title","Add menu");
        model.addAttribute(new Menu());
        return "menu/add";
    }

    @RequestMapping(value="add", method=RequestMethod.POST)
    public String processAddMenuForm(Model model, @ModelAttribute @Valid Menu menu, Errors errors) {

        if (errors.hasErrors()) {
            return "menu/add";
        }
        menuDao.save(menu);
        return "redirect:view/" + menu.getId();
    }
    @RequestMapping(value="view/{id}", method=RequestMethod.GET)
    public String viewMenu(Model model, @PathVariable int id) {

        model.addAttribute("menu", menuDao.findOne(id));
        return "menu/view";
    }

    @RequestMapping(value="add-item/{menuId}", method=RequestMethod.GET)
    public String addItem(Model model, @PathVariable int menuId) {
        Menu menuToEdit = menuDao.findOne(menuId);
        AddMenuItemForm newMenuItemForm = new AddMenuItemForm(menuToEdit, cheeseDao.findAll());

        String menuName = menuToEdit.getName(); //
        model.addAttribute("form", newMenuItemForm);
        model.addAttribute("title", "Add item to Menu: " + menuName);

        return "menu/add-item";

    }

    @RequestMapping(value= "add-item", method = RequestMethod.POST)
    public String addItem(Model model, @ModelAttribute @Valid AddMenuItemForm form, Errors errors){

        if (errors.hasErrors()) {
            return "menu/add-item";
        }
        Cheese cheese= cheeseDao.findOne(form.getCheeseId());
        Menu menu=menuDao.findOne(form.getMenuId());
        menu.addItem(cheese);
        menuDao.save(menu);

        return "redirect:/menu/view/"+menu.getId();

    }

}
