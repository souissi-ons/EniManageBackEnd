package tn.enicarthage.enimanage.DTO;


import lombok.Data;

@Data
public class ValidateTokenResponse {
    private boolean valid;

    public ValidateTokenResponse(boolean valid) {
        this.valid = valid;
    }
}