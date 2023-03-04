package com.bolsadeideas.springboot.app.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bolsadeideas.springboot.app.models.dao.IContactoDao;
import com.bolsadeideas.springboot.app.models.entity.Contacto;

@Service
public class ContactoServiceImpl implements IContactoService {

	@Autowired
	private IContactoDao contactoDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<Contacto> findAll() {
		// TODO Auto-generated method stub
		return (List<Contacto>) contactoDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Contacto> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return contactoDao.findAll(pageable);
	}

	@Override
	@Transactional
	public void save(Contacto contacto) {
		// TODO Auto-generated method stub
		contactoDao.save(contacto);

	}

	@Override
	@Transactional(readOnly = true)
	public Contacto findOne(Long id) {
		// TODO Auto-generated method stub
		return contactoDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		// TODO Auto-generated method stub
		contactoDao.deleteById(id);

	}

}
