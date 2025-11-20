/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tienda.controller;

import com.tienda.domain.Producto;
import com.tienda.service.CategoriaService;
import com.tienda.service.ProductoService;
import com.tienda.service.FirebaseStorageService;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author erick
 */
@Controller
@RequestMapping("/producto") //localhost:8080/producto
public class ProductoController {

    @Autowired
    private ProductoService productoService; //CRUD

    @Autowired
    private FirebaseStorageService firebaseStorageService; //Guardar Imagenes

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private MessageSource messageSource; //Mensaje personalizados (textos personalizados)

    @GetMapping("/listado") // https:localhost/producto/listado
    public String inicio(Model model) {
        var productos = productoService.getProductos(false); //obtiene la lista de productos
        model.addAttribute("productos", productos); //aqui paso la informacion al html (productos de color verde)
        //productos = productos.
        model.addAttribute("totalProductos", productos.size());
        //totalProductos = 4 (productos.size (4))
        return "/producto/listado"; //las vistas que yo voy a crear en el html localhost:8080/producto/listado
    }

    @PostMapping("/modificar") //https:localhost/producto/modificar
    public String modificar(Producto producto, Model model) {
        producto = productoService.getProducto(producto);
        var categorias = categoriaService.getCategorias(false);
        model.addAttribute("categorias", categorias);
        model.addAttribute("producto", producto);
        return "/producto/modifica"; //la vista que tengo que generar en el html
    }

    @PostMapping("/guardar")
    public String guardar(Producto producto,
            @RequestParam MultipartFile imagenFile,
            RedirectAttributes redirectAttributes) {
        if (!imagenFile.isEmpty()) { // Si no está vacío... pasaron una imagen...
            productoService.save(producto);
            String rutaImagen = firebaseStorageService
                    .cargaImagen(
                            imagenFile,
                            "producto",
                            producto.getIdProducto());
            producto.setRutaImagen(rutaImagen);
        }
        productoService.save(producto);
        redirectAttributes.addFlashAttribute("todoOk",
                messageSource.getMessage("mensaje.actualizado",
                        null,
                        Locale.getDefault()));
        return "redirect:/producto/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(Producto producto, RedirectAttributes redirectAttributes) {
        producto = productoService.getProducto(producto);
        if (producto == null) {  // La producto no existe...
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("producto.error01",
                            null,
                            Locale.getDefault()));
        } else if (false) { // Esto se actualiza proximas semanas...
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("producto.error02",
                            null,
                            Locale.getDefault()));
        } else if (productoService.delete(producto)) {
            // Si se borró...
            redirectAttributes.addFlashAttribute("todoOk",
                    messageSource.getMessage("mensaje.eliminado",
                            null,
                            Locale.getDefault()));
        } else {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("producto.error03",
                            null,
                            Locale.getDefault()));
        }
        return "redirect:/producto/listado";
    }

    @GetMapping("/nuevo")//localhost:8080/producto/nuevo
    public String productoNuevo(Producto producto, Model model) {
        var categorias = categoriaService.getCategorias(false); // obtener categorías
        producto = productoService.getProducto(producto);
        model.addAttribute("categorias", categorias);
        model.addAttribute("producto", producto);
        //model.addAttribute("producto", new Producto()); // objeto vacío para el form
        return "/producto/modifica";
    }
}
