package es.us.dp1.lx_xy_24_25.your_game_name.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.SequenceGenerator;
import lombok.EqualsAndHashCode;

import java.util.Objects;

@MappedSuperclass
@EqualsAndHashCode(of="id")
public class BaseEntity {

	@Id
	@SequenceGenerator(name = "entity_seq", 
        sequenceName = "entity_sequence", 
        initialValue = 100)
	@GeneratedValue(strategy = GenerationType.SEQUENCE	, generator = "entity_seq")
	protected Integer id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@JsonIgnore
	public boolean isNew() {
		return this.id == null;
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity)) return false; 
        BaseEntity that = (BaseEntity) o;
        if (this.id == null || that.id == null) {
             return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : 31; 
    }
}
