package com.yunzhi.wechatService.repository;

import com.yunzhi.wechatService.entity.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface UserRepository extends PagingAndSortingRepository<User, Long>, JpaSpecificationExecutor {

  /**
   * 根据用户名查询用户
   */
  Optional<User> findByUsernameAndClient(String username, String client);
}
