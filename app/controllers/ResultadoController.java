package controllers;


import org.json.JSONException;
import org.json.JSONObject;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import recordlinkage.ArquivoComparacao;
import recordlinkage.Link;
import recordlinkage.RecordLinkageImpl;
import views.html.arquivos_salvos;
import views.html.index;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Singleton;

@Singleton
public class ResultadoController extends Controller {


    private List<Link<JSONObject>> links;
    private static final int tamnhoPagina = 20;


    public Result pagina(int i) {

        int indice = (links.size() / tamnhoPagina) * i;

        boolean hasNext = (indice + tamnhoPagina) < links.size();
        boolean hasPrev = (indice - tamnhoPagina) > 0;

        return ok(views.html.resultados.render(links.subList(i,i+20),hasPrev,hasNext,i));

    }

    public void setResultados(List<Link<JSONObject>> resultados) {
        this.links = resultados;
    }
}
