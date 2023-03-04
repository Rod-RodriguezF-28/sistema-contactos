package com.bolsadeideas.springboot.app.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bolsadeideas.springboot.app.models.entity.Contacto;
import com.bolsadeideas.springboot.app.models.service.IContactoService;
import com.bolsadeideas.springboot.app.models.service.IUploadFileService;
import com.bolsadeideas.springboot.app.util.paginator.PageRender;

import jakarta.validation.Valid;

@Controller
@SessionAttributes("contacto")
public class ContactoController {

	@Autowired
	private IContactoService contactoService;

	@Autowired
	private IUploadFileService uploadFileService;

	@GetMapping("/uploads/{fileName:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String fileName) {

		Resource recurso = null;

		try {
			recurso = uploadFileService.load(fileName);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
				.body(recurso);

	}

	@GetMapping("/ver/{id}")
	public String ver(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {

		Contacto contacto = contactoService.findOne(id);

		if (contacto == null) {
			flash.addFlashAttribute("error", "El contacto no existe en la base de datos");
			return "redirect:/listar";
		}

		model.put("contacto", contacto);
		model.put("titulo", "Detalle contacto: " + contacto.getNombre());
		return "ver";
	}

	@GetMapping({ "/listar", "/" })
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
		
		Pageable pageRequest = PageRequest.of(page, 4);

		Page<Contacto> contactos = contactoService.findAll(pageRequest);

		PageRender<Contacto> pageRender = new PageRender<>("/listar", contactos);

		model.addAttribute("titulo", "Listado de contactos");
		model.addAttribute("contactos", contactos);
		model.addAttribute("page", pageRender);

		return "listar";
	}

	@GetMapping("/form")
	public String crear(Map<String, Object> model) {

		Contacto contacto = new Contacto();

		model.put("contacto", contacto);
		model.put("titulo", "Formulario de contacto");
		
		return "form";
	}

	@GetMapping("/form/{id}")
	public String editar(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {

		Contacto contacto = null;

		if (id > 0) {

			contacto = contactoService.findOne(id);
			if (contacto == null) {
				flash.addFlashAttribute("error", "El ID del contacto no existe en la base de datos!");
				return "redirect:/listar";
			}

		} else {
			flash.addFlashAttribute("error", "El ID del contacto no puede ser cero!");
			return "redirect:/listar";

		}
		model.put("contacto", contacto);
		model.put("titulo", "Editar contacto");
		return "form";
	}

	@PostMapping("/form")
	public String guardar(@Valid Contacto contacto, BindingResult result, Model model,
			@RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status) {

		// Validar número de teléfono
		if (!contacto.getCelular().matches("^\\+\\d{2}\\s\\d{9}$")) {
			result.rejectValue("celular", "error.celular",
					"El número de teléfono debe tener el formato: +[código de país] [número] (por ejemplo, +56 912345678).");
		}

		if (result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de contacto");
			return "form";
		}

		// Validar foto
		if (!foto.isEmpty()) {

			if (contacto.getId() != null && contacto.getId() > 0 && contacto.getFoto() != null
					&& contacto.getFoto().length() > 0) {

				uploadFileService.delete(contacto.getFoto());
			}

			String uniqueFileName = null;
			try {
				uniqueFileName = uploadFileService.copy(foto);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			flash.addFlashAttribute("info", "Has subido correctamente '" + uniqueFileName + "'");

			contacto.setFoto(uniqueFileName);

		}
		String mensajeFlash = (contacto.getId() != null) ? "Contacto modificado con exito!"
				: "Contacto creado con exito!";

		contactoService.save(contacto);
		status.setComplete();
		flash.addFlashAttribute("success", mensajeFlash);
		return "redirect:listar";
	}

	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {

		if (id > 0) {
			Contacto contacto = contactoService.findOne(id);

			contactoService.delete(id);
			flash.addFlashAttribute("success", "Contacto eliminado con exito!");

			if (uploadFileService.delete(contacto.getFoto())) {
				flash.addFlashAttribute("info", "Foto " + contacto.getFoto() + " eliminada con exito");
			}

		}
		return "redirect:/listar";
	}

}
