package com.bolsadeideas.springboot.app.models.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bolsadeideas.springboot.app.models.entity.Contacto;

public interface IContactoService {

	public List<Contacto> findAll();
	
	public Page<Contacto> findAll(Pageable pageable);
	
	public void save(Contacto contacto);
	
	public Contacto findOne(Long id);
	
	public void delete(Long id);
}
