package org.br.mineradora.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyPriceDTO {

    public USDBRL USDBRL;

}
