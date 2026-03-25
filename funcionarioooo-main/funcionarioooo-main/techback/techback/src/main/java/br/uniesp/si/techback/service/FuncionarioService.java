package br.uniesp.si.techback.service;

import br.uniesp.si.techback.dto.FuncionarioRequestDTO;
import br.uniesp.si.techback.dto.FuncionarioResponseDTO;
import br.uniesp.si.techback.exception.ResourceNotFoundException;
import br.uniesp.si.techback.mapper.FuncionarioMapper;
import br.uniesp.si.techback.model.Funcionario;
import br.uniesp.si.techback.repository.FuncionarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class FuncionarioService {

    private final FuncionarioRepository repository;
    private final FuncionarioMapper mapper;

    public FuncionarioService(FuncionarioRepository repository, FuncionarioMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public FuncionarioResponseDTO criar(FuncionarioRequestDTO dto) {
        if (repository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Já existe um funcionário com este email");
        }

        Funcionario funcionario = mapper.toEntity(dto);
        Funcionario salvo = repository.save(funcionario);
        return mapper.toResponseDTO(salvo);
    }

    public Page<FuncionarioResponseDTO> listarTodos(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toResponseDTO);
    }

    public FuncionarioResponseDTO buscarPorId(Long id) {
        Funcionario funcionario = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado com id: " + id));

        return mapper.toResponseDTO(funcionario);
    }

    public FuncionarioResponseDTO atualizar(Long id, FuncionarioRequestDTO dto) {
        Funcionario funcionario = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado com id: " + id));

        repository.findByEmail(dto.getEmail()).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                throw new IllegalArgumentException("Já existe outro funcionário com este email");
            }
        });

        mapper.updateEntity(dto, funcionario);
        Funcionario atualizado = repository.save(funcionario);
        return mapper.toResponseDTO(atualizado);
    }

    public void deletar(Long id) {
        Funcionario funcionario = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado com id: " + id));

        repository.delete(funcionario);
    }
}