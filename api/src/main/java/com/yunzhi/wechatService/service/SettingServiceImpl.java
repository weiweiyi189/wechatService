package com.yunzhi.wechatService.service;

import com.yunzhi.wechatService.entity.Setting;
import com.yunzhi.wechatService.repository.SettingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;

@Service
public class SettingServiceImpl implements SettingService {
  private final SettingRepository settingRepository;
  private static final Logger logger = LoggerFactory.getLogger(SettingServiceImpl.class);

  @Autowired
  public SettingServiceImpl(SettingRepository settingRepository) {
    this.settingRepository = settingRepository;
  }

  @Override
  public Setting save(Setting setting) {
    Assert.notNull(setting.getUrl(), "url不能为空");
    Assert.notNull(setting.getClient(), "client不能为空");
    Assert.notNull(setting.getName(), "Name不能为空");
    Setting newSetting = new Setting();

    newSetting.setUrl(setting.getUrl());
    newSetting.setClient(setting.getClient());
    newSetting.setName(setting.getName());

    return this.settingRepository.save(newSetting);
  }

  @Override
  public Setting findById(@NotNull Long id) {
    return this.settingRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("找不到相关实体"));
  }

  @Override
  public Page<Setting> page(@NotNull Pageable pageable) {
    return this.settingRepository.findAll(pageable);
  }

  @Override
  public Setting update(Long id, Setting setting) {
    Setting oldSetting = this.findById(id);
    Assert.notNull(setting.getUrl(), "url不能为空");
    Assert.notNull(setting.getClient(), "client不能为空");
    Assert.notNull(setting.getName(), "Name不能为空");

    oldSetting.setClient(setting.getClient());
    oldSetting.setUrl(setting.getUrl());
    oldSetting.setName(setting.getName());
    return this.settingRepository.save(oldSetting);
  }

  @Override
  public void deleteById(Long id) {
    this.settingRepository.deleteById(id);
  }


  @Override
  public boolean existByClient(String client) {
    return this.settingRepository.findByClient(client).isPresent();
  }


}
