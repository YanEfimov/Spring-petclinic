/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.samples.petclinic.visit.Visit;
import org.springframework.samples.petclinic.visit.VisitRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Dave Syer
 */
@Controller
class VisitController {

    private final VisitRepository visits;
    private final PetRepository pets;
    private final VetRepository vets;
    private final OwnerRepository owners;
    public static int ownerid;


    public VisitController(VisitRepository visits, PetRepository pets, VetRepository vets, OwnerRepository owners) {
        this.visits = visits;
        this.pets = pets;
        this.vets = vets;
        this.owners = owners;
    }

    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }
    
    @ModelAttribute("veterinarians")
    public Collection<Vet> veters(){
    	return this.vets.findAll();
    }
    
    @ModelAttribute("pets")
    public Collection<Pet> pets(@PathVariable("ownerId") int ownerId){
    	ownerid = ownerId;
    	return this.owners.findById(ownerId).getPets();
    }

    /**
     * Called before each and every @RequestMapping annotated method.
     * 2 goals:
     * - Make sure we always have fresh data
     * - Since we do not use the session scope, make sure that Pet object always has an id
     * (Even though id is not part of the form fields)
     *
     * @param petId
     * @return Pet
     */
    @ModelAttribute("visit")
    public Visit loadPetWithVisit(@PathVariable("petId") int petId, Map<String, Object> model) {
        Pet pet = this.pets.findById(petId);
        model.put("pet", pet);
        Visit visit = new Visit();
        pet.addVisit(visit);
        return visit;
    }

    // Spring MVC calls method loadPetWithVisit(...) before initNewVisitForm is called
    @GetMapping("/owners/{ownerId}/pets/{petId}/visits/new")
    public String initNewVisitForm(@PathVariable("petId") int petId, Map<String, Object> model) {
        return "pets/createOrUpdateVisitForm";
    }

    // Spring MVC calls method loadPetWithVisit(...) before processNewVisitForm is called
    @PostMapping("/owners/{ownerId}/pets/{petId}/visits/new")
    public String processNewVisitForm(@Valid Visit visit, BindingResult result) {
    	if (result.hasErrors()) {
            return "pets/createOrUpdateVisitForm";
        } else {
        	visit.setState("ok");
            this.visits.save(visit);
            return "redirect:/owners/{ownerId}";
        }
    }
    
    @GetMapping("/owners/{ownerId}/pets/{petId}/visits/{visitId}/edit")
    public String initUpdateVisitForm(@PathVariable("petId") int petId, @PathVariable("visitId") int visitId, ModelMap model) {
    	Visit visit = this.visits.findById(visitId);
    	model.put("visit",visit);
    	return "pets/createOrUpdateVisitForm";
    }
    
    @PostMapping("/owners/{ownerId}/pets/{petId}/visits/{visitId}/edit")
    public String processUpdateVisitForm(@Valid Visit visit, BindingResult result, @PathVariable("visitId") int visitId) {
    	visit.setId(visitId);
    	visit.setState("ok");
    	if (result.hasErrors()) {
    		return "pets/createOrUpdateVisitForm";
    	} else {
    		this.visits.save(visit);
    		return "redirect:/owners/{ownerId}";
    	}
    }
    
    @GetMapping("/owners/{ownerId}/pets/{petId}/visits/{visitId}/cancel")
    public String CancelToVisit(@PathVariable("visitId") int visitId) {
    	Visit visit = this.visits.findById(visitId);
    	visit.setState("cancel");
    	this.visits.save(visit);
    	return "redirect:/owners/{ownerId}";
    }
}
