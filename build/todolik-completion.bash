#/usr/bin/env bash

_todolik_get_tasks_numbers(){
  COMPREPLY=($(compgen -W "$(todolik list | tail -n +3 | wc -l | xargs seq -s ' ')" "${COMP_WORDS[COMP_CWORD]}"))
}

_todolik_completions()
{
  local cur
  case "$COMP_CWORD" in
    "1")
      COMPREPLY=($(compgen -W "version list create delete update" "${COMP_WORDS[1]}"));;
    "2")
      case "${COMP_WORDS[1]}" in
        "delete") _todolik_get_tasks_numbers;;
        "update") _todolik_get_tasks_numbers;;
      esac ;;
  esac
}

complete -F _todolik_completions todolik