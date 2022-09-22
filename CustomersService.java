package AgendaEmMemoria.src;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomersService {
    private List<Customer> customerList = new ArrayList<>();

    public List<Customer> listCustomers() {
        return customerList.stream()
                .filter(customer -> customer.isActivated())
                .collect(Collectors.toList());
    }

    public Customer getCustomer(String cpf) throws NotFoundException {
        for(int i = 0; i < customerList.size(); i++){
            if(customerList.get(i).getCpf().equals(cpf) && customerList.get(i).isActivated()){
                return customerList.get(i);
            }
        }
        throw new NotFoundException("Usuário não encontrado a partir do CPF informado.");
    }

    public Customer addCustomer(Customer customer) throws InvalidFormatException, AlreadyExistsException {

        if(!isCpfValid(customer.getCpf())){
            throw new InvalidFormatException("Usuário não cadastrado. CPF inválido.");
        }

        if(checkCpfExist(customer.getCpf())){
            throw new AlreadyExistsException("Usuário não cadastrado. CPF já existe.");
        }

        if(!isEmailValid(customer.getEmail())){
            throw new InvalidFormatException("Usuário não cadastrado. E-mail inserido não é válido");
        }

        if(!areTelephonesValids(customer.getTelephones())){
            throw new InvalidFormatException("O número de telefone inserido está incorreto.");
        }

        customerList.add(customer);
        return customer;
    }

    public Customer editCustomer(String customerToChangeCpf, Customer customerChanges) throws NotFoundException, InvalidFormatException {

        customerList.stream()
                .filter(customer -> customer.getCpf().equals(customerToChangeCpf))
                .forEach(customer -> {
                    if(!isEmailValid(customerChanges.getEmail()))
                        try {
                            throw new InvalidFormatException("E-mail não pode ser salvo. E-mail inserido não é válido.");
                        } catch (InvalidFormatException e) {
                            throw new RuntimeException(e);
                        }
                    if(!areTelephonesValids(customerChanges.getTelephones())){
                        try {
                            throw new InvalidFormatException("O número de telefone inserido está incorreto.");
                        } catch (InvalidFormatException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    customer.setTelephones(customerChanges.getTelephones());
                    customer.setEmail(customerChanges.getEmail());
                    customer.setFullName(customerChanges.getFullName());
                    customer.setBirthDate(customerChanges.getBirthDate());
                    customer.setAddresses(customerChanges.getAddresses());
                });

        System.out.println(customerList);

        return customerChanges;
    }

     public void removeCustomer(String cpf) throws NotFoundException {

        Customer customerFound = getCustomer(cpf);
        int customerIndex = customerList.indexOf(customerFound);

        customerFound.setActivated(false);

        customerList.set(customerIndex, customerFound);
    }

    public boolean areTelephonesValids (List<Telephone> telephones) {
        for(Telephone tel :  telephones){
            String completePhoneNumber = tel.getDdd().concat(tel.getPhoneNumber());
            if(!isTelephoneValid(completePhoneNumber)){
                return false;
            }
        }
        return true;
    }

    private boolean checkCpfExist(String cpf) {
       return customerList
               .stream()
               .anyMatch(customer -> customer.getCpf().equals(cpf));
       }

    private boolean isEmailValid(String email){
        if (email != null && email.length() > 0) {
            String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(email);
            if (matcher.matches()) {
                return true;
            }
        }
        return false;
    }

    private boolean isTelephoneValid(String telephone){
        telephone = telephone.replaceAll("\\D", "");

        if(!(telephone.length() >= 10 && telephone.length() <= 11)){
            return false;
        }

        if(telephone.length() == 11 && Integer.parseInt(telephone.substring(2,3)) != 9){
            return false;
        }

        Pattern pattern = java.util.regex.Pattern.compile(telephone.charAt(0)+"{"+telephone.length()+"}");
        Matcher matcher = pattern.matcher(telephone);
        if(matcher.find()){
            return false;
        }

        Integer[] codigosDDD = {
                11, 12, 13, 14, 15, 16, 17, 18, 19,
                21, 22, 24, 27, 28, 31, 32, 33, 34,
                35, 37, 38, 41, 42, 43, 44, 45, 46,
                47, 48, 49, 51, 53, 54, 55, 61, 62,
                64, 63, 65, 66, 67, 68, 69, 71, 73,
                74, 75, 77, 79, 81, 82, 83, 84, 85,
                86, 87, 88, 89, 91, 92, 93, 94, 95,
                96, 97, 98, 99};
        if(java.util.Arrays.asList(codigosDDD).indexOf(Integer.parseInt(telephone.substring(0,2))) == -1){
            return false;
        }

        Integer[] prefixo = {2, 3, 4, 5, 7};
        if(telephone.length() == 10 && java.util.Arrays.asList(prefixo).indexOf(Integer.parseInt(telephone.substring(2,3))) == -1){
            return false;
        }

        return true;
    }

    private boolean isCpfValid(String cpf){

        return cpf.matches("([0-9]{2}[\\.]?[0-9]{3}[\\.]?[0-9]{3}[\\/]?[0-9]{4}[-]?[0-9]{2})|([0-9]{3}[\\.]?[0-9]{3}[\\.]?[0-9]{3}[-]?[0-9]{2})");
    }
}
