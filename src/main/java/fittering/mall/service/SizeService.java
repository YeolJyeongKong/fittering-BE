package fittering.mall.service;

import fittering.mall.domain.mapper.SizeMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import fittering.mall.domain.dto.service.BottomDto;
import fittering.mall.domain.dto.service.DressDto;
import fittering.mall.domain.dto.service.OuterDto;
import fittering.mall.domain.dto.service.TopDto;
import fittering.mall.domain.entity.*;
import fittering.mall.repository.*;

@Service
@RequiredArgsConstructor
public class SizeService {

    private final SizeRepository sizeRepository;
    private final TopRepository topRepository;
    private final DressRepository dressRepository;
    private final BottomRepository bottomRepository;
    private final OuterRepository outerRepository;

    @Transactional
    public Size saveOuter(OuterDto outerDto, Product product) {
        Outer outer = outerRepository.save(SizeMapper.INSTANCE.toOuter(outerDto));
        return sizeRepository.save(Size.builder()
                                    .name(outerDto.getName())
                                    .outer(outer)
                                    .product(product)
                                    .build());
    }

    @Transactional
    public Size saveTop(TopDto topDto, Product product) {
        Top top = topRepository.save(SizeMapper.INSTANCE.toTop(topDto));
        return sizeRepository.save(Size.builder()
                                    .name(topDto.getName())
                                    .top(top)
                                    .product(product)
                                    .build());
    }

    @Transactional
    public Size saveDress(DressDto dressDto, Product product) {
        Dress dress = dressRepository.save(SizeMapper.INSTANCE.toDress(dressDto));
        return sizeRepository.save(Size.builder()
                                    .name(dressDto.getName())
                                    .dress(dress)
                                    .product(product)
                                    .build());
    }

    @Transactional
    public Size saveBottom(BottomDto bottomDto, Product product) {
        Bottom bottom = bottomRepository.save(SizeMapper.INSTANCE.toBottom(bottomDto));
        return sizeRepository.save(Size.builder()
                                    .name(bottomDto.getName())
                                    .bottom(bottom)
                                    .product(product)
                                    .build());
    }
}
