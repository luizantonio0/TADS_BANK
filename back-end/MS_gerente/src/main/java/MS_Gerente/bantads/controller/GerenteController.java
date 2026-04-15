package main.java.MS_Gerente.bantads.controller;

import main.java.MS_Gerente.bantads.model.Gerente;
import main.java.MS_Gerente.bantads.service.GerenteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/gerente")
public class GerenteController {

    final GerenteService gerenteService;

    public GerenteController(GerenteService gerenteService) {
        this.gerenteService = gerenteService;
    }
    
    @GetMapping
    public ResponseEntity<List<Gerente>> findAll(){
        return new ResponseEntity<>(gerenteService.findAll(), HttpStatus.OK);
    }

    @GetMapping("product/{id}")
    public ResponseEntity<Gerente> findById(@PathVariable UUID id){
        return new ResponseEntity<>(gerenteService.findById(id), HttpStatus.OK);
    }

    @PostMapping("product")
    public ResponseEntity<Gerente> save(@RequestBody Gerente gerente){
        return new ResponseEntity<>(gerenteService.save(gerente), HttpStatus.CREATED);
    }
    
    @PutMapping("product")
    public ResponseEntity<Gerente> update(@RequestBody Gerente gerente){
        return new ResponseEntity<>(gerenteService.update(gerente), HttpStatus.OK);
    }
    
    @DeleteMapping("product/{id}")
    public void delete(@PathVariable UUID id){
        gerenteService.deleteById(id);
    }
}        
        