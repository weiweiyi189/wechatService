package com.yunzhi.wechatService.controller;

import com.yunzhi.wechatService.entity.Setting;
import com.yunzhi.wechatService.service.SettingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "setting")
public class SettingController {
  private final SettingService settingService;

  public SettingController(SettingService settingService) {
    this.settingService = settingService;
  }

  @GetMapping("existByClient")
  public boolean existByUsername(@RequestParam String secret) {
    return this.settingService.existByClient(secret);
  }

  @GetMapping("page")
  public Page<Setting> page(
      @SortDefault.SortDefaults(@SortDefault(sort = "id", direction = Sort.Direction.DESC)) Pageable pageable
  ) {
    return this.settingService.page(pageable);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Setting save(@RequestBody Setting setting) {
    return this.settingService.save(setting);
  }

  @DeleteMapping("{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    this.settingService.deleteById(id);
  }

  @GetMapping("{id}")
  public Setting getById(@PathVariable Long id) {
    return this.settingService.findById(id);
  }

  @PutMapping("{id}")
  public Setting update(@PathVariable Long id, @RequestBody Setting setting) {
    return this.settingService.update(id, setting);
  }
}
