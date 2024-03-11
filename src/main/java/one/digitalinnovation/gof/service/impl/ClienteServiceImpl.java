package one.digitalinnovation.gof.service.impl;

import one.digitalinnovation.gof.handler.BusinessException;
import one.digitalinnovation.gof.handler.CampoObrigatorioException;
import one.digitalinnovation.gof.model.Cliente;
import one.digitalinnovation.gof.model.ClienteRepository;
import one.digitalinnovation.gof.model.Endereco;
import one.digitalinnovation.gof.model.EnderecoRepository;
import one.digitalinnovation.gof.service.ClienteService;
import one.digitalinnovation.gof.service.ViaCepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {

  @Autowired
  private ClienteRepository clienteRepository;
  @Autowired
  private EnderecoRepository enderecoRepository;
  @Autowired
  private ViaCepService viaCepService;

  @Override
  public Iterable<Cliente> buscarTodos() {
    return clienteRepository.findAll();
  }

  @Override
  public Cliente buscarPorId(Long id) {
    return clienteExiste(id);
  }

  @Override
  public void inserir(Cliente cliente) {
    verificarCampos(cliente);
    salvarClienteComCep(cliente);
  }

  @Override
  public void atualizar(Long id, Cliente cliente) {
    if (cliente.getId() == null) {
      throw new CampoObrigatorioException("id");
    }
    verificarCampos(cliente);

    clienteExiste(id);
    salvarClienteComCep(cliente);
  }

  @Override
  public void deletar(Long id) {
    clienteExiste(id);
    clienteRepository.deleteById(id);
  }

  private void verificarCampos(Cliente cliente) {
    if (cliente.getNome() == null) {
      throw new CampoObrigatorioException("nome");
    } else if (cliente.getEndereco().getCep() == null) {
      throw new CampoObrigatorioException("cep");
    }
  }

  private Cliente clienteExiste(Long id) {
    Optional<Cliente> clienteBd = clienteRepository.findById(id);
    if (!clienteBd.isPresent()) {
      throw new BusinessException("Cliente não encontrado");
    }

    return clienteBd.get();
  }

  private void salvarClienteComCep(Cliente cliente) {
    String cep = cliente.getEndereco().getCep();
    Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
      Endereco novoEndereco = viaCepService.consultarCep(cep);
      enderecoRepository.save(novoEndereco);
      return novoEndereco;
    });

    cliente.setEndereco(endereco);
    clienteRepository.save(cliente);
  }
}
