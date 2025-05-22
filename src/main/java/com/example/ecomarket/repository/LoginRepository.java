package com.example.ecomarket.repository;

    /*Importacioes a ocupar para extraer desde el model al repository e importacion de spring */
    import com.example.ecomarket.model.LoginModel;
    import org.springframework.stereotype.Repository;
    
    /*importacion de herramientas de java como List(para listar) y ArrayList(para recorrer la lista) */
    import java.util.ArrayList;
    import java.util.List;
    
    
    @Repository
    public class LoginRepository {
    
        /*Creacion de ArrayList para recorrer los login*/
        private List<LoginModel> listaLogin = new ArrayList <>();
    
        /*Metodo para retornar los Login */
        public LoginRepository (List<LoginModel> listarLogins) {
            this.listaLogin = listarLogins;
        }
    
        public List<LoginModel> obtenerLogin(){
            return listaLogin;
        }
    
        /*Buscar Login por rut*/
        public LoginModel buscarPorRut (String rut) {
            for (LoginModel login: listaLogin){
                if(login.getRut() == rut){
                    return login;
                }
            }
            return null;
        }
    
        /*Guardar Login enviandolo a la lista y retornandolo */
        public LoginModel guardar(LoginModel lib){
            listaLogin.add(lib);
            return lib;
        }
    
        /*Creacion de metodo para eliminar un login*/
        public void Eliminar (String rut){
            LoginModel login = buscarPorRut(rut);
            if(login !=null){
                listaLogin.remove(login);
            }
        
        /*Segundo metodo para eliminar*/
        int rutPosicion = 0;
        for (int i = 0; 1 < listaLogin.size(); i++){
            if(listaLogin.get(i).getRut() == rut){
                rutPosicion = 1;
                break;
            }
        }
            if (rutPosicion > 0){
                listaLogin.remove(rutPosicion);
            }
    
            /*Ultima forma para eliminar*/
            listaLogin.removeIf(x -> x.getRut() == rut);
        }
    
        /*Metodo para actualizar el login*/
        public LoginModel actualizar(LoginModel log) {
            int rutPosicion = 0;
        
            for (int i = 0; i < listaLogin.size(); i++) {
                if (listaLogin.get(i).getRut().equals(log.getRut())) {
                    rutPosicion = i;
                    break;
                }
            }
        
            if (rutPosicion == 0) {
                return null;
            }
        
            LoginModel login1 = new LoginModel();
            login1.setRut(log.getRut());
            login1.setNombreP(log.getNombreP());
            login1.setNombreM(log.getNombreM());
            login1.setApellidoP(log.getApellidoP());
            login1.setApellidoM(log.getApellidoM());
            login1.setCelurlar(log.getCelurlar());
            login1.setCodigoPostal(log.getCodigoPostal());
            login1.setCorreoElectronico(log.getCorreoElectronico());
            login1.setDireccion(log.getDireccion());
        
            listaLogin.set(rutPosicion, login1);
            return login1;
        }
        
        
    }
    
