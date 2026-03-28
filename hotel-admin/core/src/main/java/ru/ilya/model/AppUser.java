package ru.ilya.model;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name= "app_user")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_user_seq")
    @SequenceGenerator(name = "app_user_seq", sequenceName = "app_user_id_seq", allocationSize = 1)
    private Integer id;

    private String login;

    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    public AppUser(){
    }

    public AppUser(String login, String password, UserRole role){
        this.login=login;
        this.password=password;
        this.role=role;
    }

    public AppUser(Integer id, String login, String password, UserRole role) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.role = role;
    }

    public Integer getId(){
        return id;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o){
        if (this==o){
            return true;
        }
        if (!(o instanceof AppUser)){
            return false;
        }
        AppUser appUser = (AppUser) o;
        return Objects.equals(id, appUser.id);
    }

    @Override
    public int hashCode(){
        return Objects.hash(id);
    }
}
