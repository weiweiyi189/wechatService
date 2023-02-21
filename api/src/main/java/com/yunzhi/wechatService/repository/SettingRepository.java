package com.yunzhi.wechatService.repository;

import com.yunzhi.wechatService.entity.Setting;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface SettingRepository extends PagingAndSortingRepository<Setting, Long>, JpaSpecificationExecutor<Setting> {

  Optional<Setting> findByClient(String client);

}
