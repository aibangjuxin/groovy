
- I using lazyvim 
- Using Space trigger shortcut 
- Reference:
 - 按照提示的哪些快捷方式可以去尝试了


" make my backup init.vim
" 基本配置
set number          " 显示行号
set relativenumber  " 显示相对行号
set tabstop=4       " Tab 长度设置为 4 个空格
set shiftwidth=4    " 自动缩进为 4 个空格
set expandtab       " 将 Tab 转换为空格

" 启用语法高亮
syntax on

" 安装 vim-plug 插件管理器
call plug#begin('~/.config/nvim/plugged')
Plug 'junegunn/vim-easy-align'
Plug 'tpope/vim-surround'
Plug 'preservim/nerdtree'
Plug 'morhetz/gruvbox'
" 添加 nvim-tree 插件
Plug 'nvim-tree/nvim-tree.lua'
Plug 'nvim-tree/nvim-web-devicons'
call plug#end()

" 主题设置
colorscheme gruvbox
lua << EOF
-- OR setup with some options
require("nvim-tree").setup({
  sort = {
    sorter = "case_sensitive",
  },
  view = {
    width = 30,
  },
  renderer = {
    group_empty = true,
  },
  filters = {
    dotfiles = true,
  },
})
-- EOF

require'nvim-web-devicons'.setup {
 -- your personnal icons can go here (to override)
 -- you can specify color or cterm_color instead of specifying both of them
 -- DevIcon will be appended to `name`
 override = {
  zsh = {
    icon = "",
    color = "#428850",
    cterm_color = "65",
    name = "Zsh"
  }
 };
 -- globally enable different highlight colors per icon (default to true)
 -- if set to false all icons will have the default icon's color
 color_icons = true;
 -- globally enable default icons (default to false)
 -- will get overriden by `get_icons` option
 default = true;
 -- globally enable "strict" selection of icons - icon will be looked up in
 -- different tables, first by filename, and if not found by extension; this
 -- prevents cases when file doesn't have any extension but still gets some icon
 -- because its name happened to match some extension (default to false)
 strict = true;
 -- same as `override` but specifically for overrides by filename
 -- takes effect when `strict` is true
 override_by_filename = {
  [".gitignore"] = {
    icon = "",
    color = "#f1502f",
    name = "Gitignore"
  }
 };
 -- same as `override` but specifically for overrides by extension
 -- takes effect when `strict` is true
 override_by_extension = {
  ["log"] = {
    icon = "",
    color = "#81e043",
    name = "Log"
  }
 };
 -- same as `override` but specifically for operating system
 -- takes effect when `strict` is true
 override_by_operating_system = {
  ["apple"] = {
    icon = "",
    color = "#A2AAAD",
    cterm_color = "248",
    name = "Apple",
  },
 };
}
EOF
nnoremap <C-n> :NvimTreeToggle<CR>


# Using lazyvim managed my plug 
- need notice install different 
- Reference
  - https://github.com/nvim-tree/nvim-tree.lua/wiki/Installation
  - packer 
  - vim-plug
  - lazy


如果你想从本地安装或加载 Neovim 插件，可以在 `init.vim` 文件中使用本地路径。以下是一个示例配置，假设插件位于 `~/my_plugins` 目录下：

```vim
call plug#begin('~/.config/nvim/plugged')

" 使用绝对路径加载本地插件
Plug '~/my_plugins/my_plugin'

call plug#end()
```

下面是更详细的说明：

1. `call plug#begin('~/.config/nvim/plugged')` 指定插件安装目录。
2. `Plug '~/my_plugins/my_plugin'` 使用 `Plug` 命令加载位于 `~/my_plugins` 目录下的 `my_plugin` 插件。这里使用的是本地的绝对路径。
3. `call plug#end()` 结束插件管理块。

确保 `~/.config/nvim/init.vim` 中包含这些配置后，运行 `:PlugInstall` 命令来安装插件。Neovim 会从指定的本地路径加载插件。

