package com.bolsadeideas.springboot.app.models.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bolsadeideas.springboot.app.models.entity.Contacto;

public interface IContactoDao extends JpaRepository<Contacto, Long> {

}
