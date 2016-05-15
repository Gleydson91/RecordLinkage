package controllers;

import models.Usuario;
import play.api.data.Form;
import play.mvc.*;
import javax.inject.*;
import views.html.*;


public class HomeController extends Controller {


    public Result index() {
        return ok(index.render());
    }

    public Result arquivosSalvos() {
        return ok(arquivos_salvos.render());
    }

    public Result compararArquivos() {
        System.out.println("ha");
        return ok();
    }

}
