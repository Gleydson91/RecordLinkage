package models;

import javax.persistence.*;

import com.avaje.ebean.Model;

/**
 * Created by gleydson on 14/05/16.
 */
@Entity
public class Usuario extends Model{

    public Long id;

    public String nome;

    public String username;

    public String senha;


}
