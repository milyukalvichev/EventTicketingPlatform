package com.ticketing.main_app.controller;

import com.ticketing.main_app.dto.PromoDTO;
import com.ticketing.main_app.service.AdminPromoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/promos")
public class AdminPromoController {

    private final AdminPromoService promoService;

    public AdminPromoController(AdminPromoService promoService) {
        this.promoService = promoService;
    }

    @GetMapping
    public String listPromos(Model model) {
        model.addAttribute("promos", promoService.getAllPromos());
        return "admin-promos";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("promoDTO")) {
            model.addAttribute("promoDTO", new PromoDTO());
        }
        return "promo-create";
    }

    @PostMapping("/create")
    public String createPromo(@Valid @ModelAttribute("promoDTO") PromoDTO promoDTO,
                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "promo-create";
        }
        promoService.createPromo(promoDTO);
        return "redirect:/admin/promos?created=true";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        PromoDTO dto = promoService.getPromoById(id);
        model.addAttribute("promoDTO", dto);
        model.addAttribute("promoId", id);
        return "promo-edit";
    }

    @PostMapping("/edit/{id}")
    public String updatePromo(@PathVariable Long id,
                              @Valid @ModelAttribute("promoDTO") PromoDTO promoDTO,
                              BindingResult bindingResult,
                              Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("promoId", id);
            return "promo-edit";
        }
        promoService.updatePromo(id, promoDTO);
        return "redirect:/admin/promos?updated=true";
    }

    @PostMapping("/delete/{id}")
    public String deletePromo(@PathVariable Long id) {
        promoService.deletePromo(id);
        return "redirect:/admin/promos?deleted=true";
    }
}