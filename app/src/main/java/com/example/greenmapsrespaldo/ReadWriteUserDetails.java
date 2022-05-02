package com.example.greenmapsrespaldo;

public class ReadWriteUserDetails {

    private String usuario;
    private String phone;

    public ReadWriteUserDetails() {

    }

    public ReadWriteUserDetails(String usuario, String phone) {
        this.usuario = usuario;
        this.phone = phone;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "ReadWriteUserDetails{" +
                "usuario='" + usuario + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
