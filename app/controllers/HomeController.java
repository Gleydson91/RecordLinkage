package controllers;


import com.fasterxml.jackson.databind.util.JSONPObject;
import models.Usuario;
import play.mvc.*;
import javax.inject.*;

import play.twirl.api.Content;
import recordlinkage.ArquivoComparacao;
import recordlinkage.Link;
import recordlinkage.RecordLinkageImpl;
import scala.collection.immutable.Page;
import views.html.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;


public class HomeController extends Controller {

    @Inject
    ResultadoController resultadoController;

    public Result index() {
        return ok(index.render(null));
    }

    public Result arquivosSalvos() {
        return ok(arquivos_salvos.render());
    }

    public Result compararArquivos() {
        Http.MultipartFormData<File> body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> f1 = body.getFile("file1");
        Http.MultipartFormData.FilePart<File> f2 = body.getFile("file2");

        if (f1 != null && f2!=null) {
            java.io.File file1 = f1.getFile();
            java.io.File file2 = f2.getFile();

            List<Link<JSONObject>> res;
            try {
               res = chamarRecordLinkage(file1,file2);
            } catch (IOException e) {
                e.printStackTrace();
                return  ok(index.render("Ocorreu um erro ao executar código."));
            }

            resultadoController.setResultados(res);

            return redirect(routes.ResultadoController.pagina(0));
        } else {
            return  ok(index.render("Arquivos não puderam ser lidos"));
        }

    }

    private List<Link<JSONObject>> chamarRecordLinkage(File file1, File file2) throws IOException, JSONException, FileNotFoundException {
        ArquivoComparacao ac1 = new ArquivoComparacao(file1, Arrays.asList("product_name","manufacturer"), "manufacturer");
        ArquivoComparacao ac2 = new ArquivoComparacao(file2, Arrays.asList("title","manufacturer"), "manufacturer");

        RecordLinkageImpl rl = new RecordLinkageImpl(ac1,ac2,true,true);
        rl.executar();

        return rl.obterResultados();
    }
}
