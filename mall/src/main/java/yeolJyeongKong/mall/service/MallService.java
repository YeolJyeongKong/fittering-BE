package yeolJyeongKong.mall.service;

import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yeolJyeongKong.mall.domain.dto.MallDto;
import yeolJyeongKong.mall.domain.entity.Mall;
import yeolJyeongKong.mall.repository.MallRepository;

@Service
@RequiredArgsConstructor
public class MallService {

    private final MallRepository mallRepository;

    public void save(MallDto mallDto) {
        mallRepository.save(new Mall(mallDto));
    }

    public Mall findByName(String mallName) {
        return mallRepository.findByName(mallName)
                .orElseThrow(() -> new NoResultException("mall dosen't exist"));
    }
}
